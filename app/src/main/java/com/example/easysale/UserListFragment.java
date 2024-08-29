package com.example.easysale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.easysale.api.ApiService;
import com.example.easysale.api.RetrofitInstance;
import com.example.easysale.data.Result;
import com.example.easysale.data.User;
import com.example.easysale.databinding.FragmentUserListBinding;
import com.example.easysale.room.UserAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserListFragment extends Fragment {

    FragmentUserListBinding fulb;
    private ArrayList<User> usersArrayList = new ArrayList<>();
    private ArrayList<User> currentUsersArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ViewModel viewModel;
    private int totalPages = 0;
    public int currentPage = 1;
    private String searchWord = "";
    private List<User> foundUsers = new ArrayList<>();
    private int userIndex = -1;
    private boolean isSearching = false;
    private int perPage = 6;
    private boolean isDragged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fulb = FragmentUserListBinding.inflate(getLayoutInflater());
        View view = fulb.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkDataOnBegin();

        Glide.with(this).load(R.drawable.search_icon).fitCenter().into(fulb.searchImageView);
        Glide.with(this).load(R.drawable.next_arrow).fitCenter().into(fulb.nextPageImageView);
        Glide.with(this).load(R.drawable.back_arrow).fitCenter().into(fulb.prevPageImageView);
        Glide.with(this).load(R.drawable.play_arrow).fitCenter().into(fulb.searchArrowImageView);

        fulb.searchArrowImageView.setImageAlpha(100);

        if (currentPage == 1)
            fulb.prevPageImageView.setImageAlpha(100);

        recyclerView = fulb.ContactRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        petchUsersPerPage();

        getAllUsers();

        fulb.searchImageView.setOnClickListener( click ->  {
            String searchPhrase =  fulb.searchEditText.getText().toString();
            if (!isSearching && !searchPhrase.equals("")) {
                isSearching = true;
                userIndex = 0;
                foundUsers.clear();
                Integer foundPage = getUserPages(searchPhrase);
                if (foundPage != -1) {
                    if (foundUsers.size() > 1)
                        fulb.searchArrowImageView.setImageAlpha(255);

                    if (foundPage != currentPage) {
                        currentPage = foundPage;
                        setUsersForCurrentPage();
                    } else {
                        searchForUser();
                        isSearching = false;
                    }
                } else {
                    Toast.makeText(getContext(), "No Results Found!", Toast.LENGTH_SHORT).show();
                    isSearching = false;
                }
            }
        });

        fulb.nextPageImageView.setOnClickListener( click -> {
            if (currentPage < totalPages) {
                currentPage++;
                setUsersForCurrentPage();
            }
        });

        fulb.prevPageImageView.setOnClickListener(click ->{
            if (currentPage > 1) {
                currentPage--;
                setUsersForCurrentPage();
            }
        });
        fulb.createUserFab.setOnClickListener(click -> {
            CreateUserFragment createUserFragment = new CreateUserFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("perPage",perPage);
            bundle.putInt("totalPages",totalPages);
            bundle.putInt("currentPage",currentPage);
            createUserFragment.setArguments(bundle);

            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.recycContainLayout, createUserFragment);
            ft.commit();
        });

        fulb.searchArrowImageView.setOnClickListener(click->{
            if (foundUsers.size() != 0 && userIndex < foundUsers.size()-1){
                userIndex++;
                currentPage = Integer.parseInt(foundUsers.get(userIndex).getPage());
                setUsersForCurrentPage();
            }
        });

        userAdapter = new UserAdapter(currentUsersArrayList, this);
        recyclerView.setAdapter(userAdapter);



        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                int fromPositionOriginal = usersArrayList.indexOf(currentUsersArrayList.get(fromPosition));
                int toPositionOriginal = usersArrayList.indexOf(currentUsersArrayList.get(toPosition));

                User fromUser = usersArrayList.get(fromPositionOriginal);
                User toUser = usersArrayList.get(toPositionOriginal);
                User swapUser = new User(fromUser);
                fromUser.updateUserFromUser(toUser);
                toUser.updateUserFromUser(swapUser);
                userAdapter.notifyItemMoved(fromPosition, toPosition);
                return false;
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG)
                    isDragged = true;
                else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && isDragged) {
                    viewModel.updateAllUsers(usersArrayList);
                    isDragged = false;
                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                alertDialog.setTitle("Confirmation Required!").setMessage("Please Confirm User Deletion").setIcon(R.drawable.warning_sign).setCancelable(false)
                        .setNegativeButton("Cancel", (dialogInterface, negClick) -> {
                            userAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            dialogInterface.cancel();
                        }).setPositiveButton("Confirm", (dialogInterface, negClick) -> {
                            int position = viewHolder.getAdapterPosition();
                            User user = currentUsersArrayList.get(position);
                            viewModel.deleteUser(user);
                            userAdapter.notifyItemRemoved(position);
                        });
                alertDialog.create().show();
            }
        }).attachToRecyclerView(recyclerView);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saveData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("currentActivity", -1);
                editor.apply();

                Intent intent = new Intent(getContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private Integer getUserPages(String searchPhrase) {
        synchronized (usersArrayList) {
            for (User user : usersArrayList) {
                if (user.getFirstName().toLowerCase().contains(searchPhrase.toLowerCase()) || user.getLastName().toLowerCase().contains(searchPhrase.toLowerCase())) {
                    searchWord = searchPhrase;
                    foundUsers.add(user);
                }
            }
        }
        if (foundUsers.size() != 0)
            return Integer.valueOf(foundUsers.get(0).getPage());
        else return -1;
    }

    private void markDataRecived(Integer status){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saveData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("status", status);
        editor.apply();
    }

    private Integer checkIfDataRecived(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saveData", Context.MODE_PRIVATE);
        Integer status = sharedPreferences.getInt("status", -1);
        return status;
    }

    private void petchUsersPerPage(){
        Integer status = checkIfDataRecived();
        if (status == -1){
            viewModel.petchUsersPerPage(this,totalPages);
        } else totalPages = status;

    }

    private void getAllUsers(){
        viewModel.getAllUsersFromDB().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                if (users.size() > 0) {
                    usersArrayList.clear();
                    usersArrayList.addAll(users);
                    setUsersForCurrentPage();
                } else {
                    usersArrayList.clear();
                    currentUsersArrayList.clear();
                    userAdapter.notifyDataSetChanged();
                    adjustButtonsAlpha();
                }
            }
        });
    }

    private void setUsersForCurrentPage(){
        currentUsersArrayList.clear();
        synchronized (usersArrayList) {
            for (User user : usersArrayList) {
                if (Integer.parseInt(user.getPage()) == currentPage)
                    currentUsersArrayList.add(user);
            }
        }
        if (currentUsersArrayList.size() != 0) {
            fulb.pageTextView.setText(String.valueOf(currentPage));
            adjustButtonsAlpha();
            userAdapter.notifyDataSetChanged();

            if (isSearching) {
                searchForUser();
                if (userIndex == foundUsers.size()-1){
                    foundUsers.clear();
                    fulb.searchArrowImageView.setImageAlpha(100);
                    searchWord = "";
                    isSearching = false;
                }
            }

        } else migratePagesDown();
    }

    private void adjustButtonsAlpha(){
        if (currentPage == 1) {
            fulb.prevPageImageView.setImageAlpha(100);
        } else fulb.prevPageImageView.setImageAlpha(255);

        if (currentPage == totalPages){
            fulb.nextPageImageView.setImageAlpha(100);
        } else fulb.nextPageImageView.setImageAlpha(255);
    }

    private void searchForUser(){
        User user;
        boolean isFound = false;
        synchronized (currentUsersArrayList) {
            for (int i = 0; i < currentUsersArrayList.size(); i++) {
                user = currentUsersArrayList.get(i);
                if (user == foundUsers.get(userIndex)) {
                    recyclerView.smoothScrollToPosition(i);
                    Toast.makeText(getContext(), "Found User: "+user.getFirstName()+" "+ user.getLastName(), Toast.LENGTH_SHORT).show();
                    isFound = true;
                    break;
                }
            }
        }
        if (!isFound)
            Toast.makeText(getContext(), "No Contacts Found!", Toast.LENGTH_SHORT).show();
    }

    private void checkDataOnBegin(){
        if (getArguments() != null) {
            int totPages = getArguments().getInt("totalPages", -1);
            int currPages = getArguments().getInt("currentPage", -1);
            String searchPhrase = getArguments().getString("searchPhrase","");
            if (searchPhrase != "")
                fulb.searchEditText.setText(searchPhrase);
            if (totPages != -1) {
                totalPages = totPages;
                markDataRecived(totalPages);
            }
            if (currPages != -1)
                currentPage = currPages;
        }
    }

    private void migratePagesDown(){
        if (currentPage < totalPages) {
            Integer page;
            synchronized (usersArrayList) {
                for (User user : usersArrayList) {
                    page = Integer.parseInt(user.getPage());
                    if (page > currentPage) {
                        page--;
                        user.setPage(String.valueOf(page));
                    }
                }
                viewModel.updateAllUsers(usersArrayList);
            }
            totalPages--;
            markDataRecived(totalPages);
        } else if (currentPage == totalPages && currentPage > 1){
            totalPages--;
            currentPage--;
            markDataRecived(totalPages);
        }
        setUsersForCurrentPage();
    }

    public void getCallback(Result result, String code){
        if (result != null && result.getUsers() != null){
            List<User> users = result.getUsers();
            if (users.size() != 0) {
                for(User user : users){
                    user.setPage(String.valueOf(totalPages+1));
                }
                viewModel.insertAllUsers(users);
                totalPages++;
                perPage = result.getPerPage();
                petchUsersPerPage();
            } else markDataRecived(totalPages);
        } else {
            if (totalPages == 0)
                Toast.makeText(getContext(), "Failed To get Data From the Server Code : "+code, Toast.LENGTH_SHORT).show();
        }
    }
    public void callbackFailed (String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saveData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentActivity", 0);
        editor.putInt("currentPage", currentPage);
        editor.putInt("totalPages", totalPages);
        if (!fulb.searchEditText.getText().toString().equals(""))
            editor.putString("searchPhrase", fulb.searchEditText.getText().toString());
        else editor.putString("searchPhrase", "");

        editor.apply();

    }
}
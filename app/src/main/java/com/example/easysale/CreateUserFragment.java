package com.example.easysale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.easysale.api.ApiService;
import com.example.easysale.api.RetrofitInstance;
import com.example.easysale.data.ManageUser;
import com.example.easysale.data.User;
import com.example.easysale.databinding.FragmentCreateUserBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateUserFragment extends Fragment {

    private FragmentCreateUserBinding fcub;
    private User user;
    private Integer status = 0;
    private ViewModel viewModel;
    private ActivityResultLauncher<Intent> resultLauncher;
    private String imageUri;
    private int perPage = 6;
    private int totalPages = 2;
    private int pageWithSpace = -1;
    private int currentPage = 1;
    private Observer observer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fcub = FragmentCreateUserBinding.inflate(getLayoutInflater());
        return fcub.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        observer = (Observer<ManageUser>) manageUser -> {
            fcub.createUserProgressBar.setVisibility(View.INVISIBLE);
            if (manageUser != null){
                Toast.makeText(getContext(), "User Created Successfully", Toast.LENGTH_SHORT).show();
                viewModel.addNewUser(user);
                status = 0;

                UserListFragment userListFragment = new UserListFragment();
                Bundle bundle = new Bundle();
                if (pageWithSpace > totalPages){
                    totalPages++;
                    bundle.putInt("totalPages", totalPages);
                }
                bundle.putInt("currentPage",pageWithSpace);
                userListFragment.setArguments(bundle);

                FragmentManager fm = requireActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.recycContainLayout, userListFragment);
                ft.commit();
            } else {
                status = 0;
                Toast.makeText(getContext(), "User Created Unsuccessfully", Toast.LENGTH_SHORT).show();
            }

        };

        Glide.with(getContext()).load(R.drawable.upload_image_normal).fitCenter().into(fcub.createImageView);

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    if (data != null){
                        imageUri = data.getDataString();
                        Glide.with(getContext()).load(imageUri).fitCenter().into(fcub.createImageView);
                        fcub.createImageView.setBorderWidth(3);
                    }
                }
            }
        });

        fcub.createImageView.setOnClickListener(click -> {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                resultLauncher.launch(galleryIntent);
        });

        assert getArguments() != null;
        perPage = getArguments().getInt("perPage",6);
        totalPages = getArguments().getInt("totalPages",2);
        currentPage = getArguments().getInt("currentPage",1);
        imageUri = getArguments().getString("imageUri","");
        fcub.createFNEditText.setText(getArguments().getString("firstName",""));
        fcub.createLNEditText.setText(getArguments().getString("lastName",""));
        fcub.createEmailEditText.setText(getArguments().getString("email",""));

        if (imageUri != ""){
            Glide.with(fcub.getRoot()).load(imageUri).fitCenter().into(fcub.createImageView);
        }

        getAllUsers();

        fcub.createButton.setOnClickListener(click -> {
            if (status == 0 && !fcub.createFNEditText.getText().toString().equals("") && !fcub.createLNEditText.getText().toString().equals("") && !fcub.createEmailEditText.getText().toString().equals("")){
                status = 1;
                String avatar = "";
                if (imageUri != null)
                    avatar = imageUri;
                user = new User(100,fcub.createEmailEditText.getText().toString(),fcub.createFNEditText.getText().toString(),fcub.createLNEditText.getText().toString()
                        ,avatar, String.valueOf(pageWithSpace));
                fcub.createUserProgressBar.setVisibility(View.VISIBLE);
                viewModel.createUser().observe(getViewLifecycleOwner(), observer);
            } else {
                fcub.createUserProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "All Fields Required to Fill!", Toast.LENGTH_SHORT).show();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saveData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("currentActivity", 0);
                editor.apply();

                UserListFragment userListFragment = new UserListFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("currentPage",currentPage);
                userListFragment.setArguments(bundle);

                FragmentManager fm = requireActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.recycContainLayout, userListFragment);
                ft.commit();
            }
        });
    }

    private void getAllUsers(){
        viewModel.getAllUsersFromDB().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                findSpaceInPage(users);
                switchVisiblity();
            }
        });
    }

    private void switchVisiblity(){
        fcub.createImageView.setVisibility(View.VISIBLE);
        fcub.createButton.setVisibility(View.VISIBLE);
        fcub.createFNEditText.setVisibility(View.VISIBLE);
        fcub.createLNEditText.setVisibility(View.VISIBLE);
        fcub.createEmailEditText.setVisibility(View.VISIBLE);
        fcub.createUserProgressBar.setVisibility(View.INVISIBLE);
    }

    private void findSpaceInPage(List<User> users){
        Integer counter = 0;
        Integer pageIndex = 1;
        boolean found = false;
        for (int i = 0; i < perPage; i++) {
            for (int j = 0; j < users.size(); j++) {
                if (Integer.parseInt(users.get(j).getPage()) == pageIndex) {
                    counter++;
                }
            }
            if (counter == perPage) {
                pageIndex++;
                counter = 0;
            } else {
                pageWithSpace = pageIndex;
                found = true;
                break;
            }
        }
        if (!found){
            pageWithSpace = totalPages+1;
        }
    }

    public void callbackFailed (String message){
        fcub.createUserProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saveData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentActivity", 2);
        editor.putInt("perPage",perPage);
        editor.putInt("totalPages",totalPages);
        editor.putInt("currentPage",currentPage);

        if (imageUri != null)
            editor.putString("imageUri",imageUri);
        else
            editor.putString("imageUri","");
        if (!fcub.createFNEditText.getText().equals(""))
            editor.putString("firstName", String.valueOf(fcub.createFNEditText.getText()));
        else editor.putString("firstName", "");
        if (!fcub.createLNEditText.getText().equals(""))
            editor.putString("lastName", String.valueOf(fcub.createLNEditText.getText()));
        else
            editor.putString("lastName", "");
        if (!fcub.createEmailEditText.getText().equals(""))
            editor.putString("email", String.valueOf(fcub.createEmailEditText.getText()));
        else
            editor.putString("email", "");
        editor.apply();
    }
}
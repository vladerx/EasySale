package com.example.easysale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.easysale.api.ApiService;
import com.example.easysale.api.RetrofitInstance;
import com.example.easysale.data.ManageUser;
import com.example.easysale.data.User;
import com.example.easysale.databinding.FragmentUpdateUserBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpdateUserFragment extends Fragment {

    FragmentUpdateUserBinding fuub;
    private User tempUser;
    private int status = 0;
    private ViewModel viewModel;
    private int currentPage;
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fuub = FragmentUpdateUserBinding.inflate(getLayoutInflater());
        View view = fuub.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);


        Glide.with(getContext()).load(R.drawable.upload_image_normal).fitCenter().into(fuub.updateImageView);

        assert getArguments() != null;
        String email = getArguments().getString("clickedEmail");
        String firstName = getArguments().getString("clickedFirstName");
        String lastName = getArguments().getString("clickedLastName");
        String avatar = getArguments().getString("clickedAvatar");
        currentPage = getArguments().getInt("currentPage");
        tempUser = new User(getArguments().getInt("clickedId"),email,firstName,lastName ,avatar,getArguments().getString("clickedPage"));
        tempUser.setUserId(getArguments().getInt("clickedUserId"));
        fuub.updateFNEditText.setText(firstName);
        fuub.updateLNEditText.setText(lastName);
        fuub.updateEmailEditText.setText(email);
        String imageUri = getArguments().getString("imageUri");

        if (imageUri != null && !imageUri.equals(""))
            Glide.with(getContext()).load(imageUri).fitCenter().into(fuub.updateImageView);
        else if (!avatar.equals(""))
            Glide.with(getContext()).load(avatar).fitCenter().into(fuub.updateImageView);
        else Glide.with(getContext()).load(R.drawable.bag).fitCenter().into(fuub.updateImageView);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saveData", Context.MODE_PRIVATE);
        Integer stat = sharedPreferences.getInt("currentActivity", -1);

        if (stat != 1){
            viewModel.getUser(this);
        } else switchVisiblity();

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    if (data != null){
                        String imageUri = data.getDataString();
                        Glide.with(getContext()).load(imageUri).fitCenter().into(fuub.updateImageView);
                        tempUser.setAvatar(imageUri);
                    }
                }
            }
        });

        fuub.updateImageView.setOnClickListener( click ->{
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            resultLauncher.launch(galleryIntent);
        });

        fuub.updateButton.setOnClickListener(click -> {
            if (status == 0 && !fuub.updateFNEditText.getText().toString().equals("") && !fuub.updateLNEditText.getText().toString().equals("") && !fuub.updateEmailEditText.getText().toString().equals("")){
                fuub.updateUserProgressBar.setVisibility(View.VISIBLE);
                status = 1;
                tempUser.setFirstName(fuub.updateFNEditText.getText().toString());
                tempUser.setLastName(fuub.updateLNEditText.getText().toString());
                tempUser.setEmail(fuub.updateEmailEditText.getText().toString());
                viewModel.updateUser(this);
            } else {
                fuub.updateUserProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "All Fields Required to Fill!", Toast.LENGTH_SHORT).show();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goBack();
            }
        });
    }

    private void goBack(){
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
        ft.replace(R.id.recycContainLayout,userListFragment );
        ft.commit();
    }

    private void switchVisiblity(){
        fuub.updateImageView.setVisibility(View.VISIBLE);
        fuub.uploadTextView.setVisibility(View.VISIBLE);
        fuub.updateButton.setVisibility(View.VISIBLE);
        fuub.updateFNEditText.setVisibility(View.VISIBLE);
        fuub.updateLNEditText.setVisibility(View.VISIBLE);
        fuub.updateEmailEditText.setVisibility(View.VISIBLE);
        fuub.updateUserProgressBar.setVisibility(View.INVISIBLE);
    }

    public void getUserCallback(String code){
        switchVisiblity();
        Toast.makeText(getActivity(), "Code : "+ code + " : User Fetched Successfully!", Toast.LENGTH_SHORT).show();
    }

    public void userCallbackFailed(String code){
        Toast.makeText(getActivity(), "Code : "+ code + " : User Fetched Unsuccessfully!", Toast.LENGTH_SHORT).show();
        goBack();
    }

    public void getCallback(ManageUser manageUser, String code){
        fuub.updateUserProgressBar.setVisibility(View.INVISIBLE);
        String codeResponse = " : Successful Update!";
        if (manageUser != null){
            viewModel.updateUser(tempUser);
            status = 0;
            UserListFragment userListFragment = new UserListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("currentPage",currentPage);
            userListFragment.setArguments(bundle);

            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.recycContainLayout,userListFragment );
            ft.commit();
        } else {
            codeResponse = " : Error Occurred While Updating User!";
            status = 0;
        }
        Toast.makeText(getContext(), "Code : "+ code + codeResponse, Toast.LENGTH_SHORT).show();
    }

    public void callbackFailed (String message){
        fuub.updateUserProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saveData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentActivity",1);
        editor.putInt("clickedId",tempUser.getId());
        editor.putInt("clickedUserId",tempUser.getUserId());
        editor.putString("clickedAvatar",tempUser.getAvatar());
        editor.putString("clickedPage",tempUser.getPage());
        editor.putInt("currentPage",currentPage);
        editor.putString("imageUri", tempUser.getAvatar());

        if (!fuub.updateFNEditText.getText().equals(""))
            editor.putString("clickedFirstName", String.valueOf(fuub.updateFNEditText.getText()));
        else editor.putString("clickedFirstName",tempUser.getFirstName());
        if (!fuub.updateLNEditText.getText().equals(""))
            editor.putString("clickedLastName", String.valueOf(fuub.updateLNEditText.getText()));
        else editor.putString("clickedLastName",tempUser.getLastName());
        if (!fuub.updateEmailEditText.getText().equals(""))
            editor.putString("clickedEmail", String.valueOf(fuub.updateEmailEditText.getText()));
        else editor.putString("clickedEmail",tempUser.getEmail());
        editor.apply();
    }
}
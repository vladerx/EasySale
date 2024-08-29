package com.example.easysale;


import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.easysale.api.ApiService;
import com.example.easysale.api.RetrofitInstance;
import com.example.easysale.data.ManageUser;
import com.example.easysale.data.Result;
import com.example.easysale.data.User;
import com.example.easysale.data.UserResponse;
import com.example.easysale.room.UserDao;
import com.example.easysale.room.UserDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private ArrayList<User> users = new ArrayList<>();
    private final UserDao userDao;
    private Application application;
    private Activity currentActivity;
    private CreateUserFragment createUserFragment;
    private UpdateUserFragment  updateUserFragment;
    private UserListFragment userListFragment;

    public UserRepository(Application application) {
        this.application = application;
        UserDatabase userDatabase = UserDatabase.getInstance(application);
        this.userDao = userDatabase.getUserDao();
    }
    //api

    public void loginUser(String email, String password, LoginActivity loginActivity){
        currentActivity = loginActivity;
        ApiService apiService = RetrofitInstance.getService();
        Call<ManageUser> call = apiService.loginUser(email, password);
        call.enqueue(new Callback<ManageUser>() {
            @Override
            public void onResponse(Call<ManageUser> call, Response<ManageUser> response) {
                ((LoginActivity) currentActivity).getCallback(response.body(), String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<ManageUser> call, Throwable throwable) {
                Toast.makeText(application.getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void registerUser(String email, String password, LoginActivity loginActivity){
        currentActivity = loginActivity;
        ApiService apiService = RetrofitInstance.getService();
        Call<ManageUser> call = apiService.registerNewUser(email, password);
        call.enqueue(new Callback<ManageUser>() {
            @Override
            public void onResponse(Call<ManageUser> call, Response<ManageUser> response) {
                ((LoginActivity) currentActivity).getCallback(response.body(), String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<ManageUser> call, Throwable throwable) {
                ((LoginActivity) currentActivity).callbackFailed(throwable.getMessage());
            }
        });
    }

    public void petchUsersPerPage(UserListFragment userListFragment, int totalPages){
        this.userListFragment = userListFragment;
        ApiService apiService = RetrofitInstance.getService();
        Call<Result> call = apiService.getPageData(String.valueOf((totalPages+1)));
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                userListFragment.getCallback(result,String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<Result> call, Throwable throwable) {
                userListFragment.callbackFailed(throwable.getMessage());
            }
        });
    }
    public void createUser(CreateUserFragment  createUserFragment){
        this.createUserFragment = createUserFragment;
        ApiService apiService = RetrofitInstance.getService();
        Call<ManageUser> call = apiService.createUser("morpheus","leader");
        call.enqueue(new Callback<ManageUser>() {
            @Override
            public void onResponse(Call<ManageUser> call, Response<ManageUser> response) {
                ManageUser manageUser = response.body();
                createUserFragment.getCallback(manageUser,String.valueOf(response.code()));
            }
            @Override
            public void onFailure(Call<ManageUser> call, Throwable throwable) {
                createUserFragment.callbackFailed(throwable.getMessage());
            }
        });
    }
    public void updateUser(UpdateUserFragment  updateUserFragment){
        this.updateUserFragment = updateUserFragment;
        ApiService apiService = RetrofitInstance.getService();
        Call<ManageUser> call = apiService.updateUser("2","morpheus","zion resident");
        call.enqueue(new Callback<ManageUser>() {
            @Override
            public void onResponse(Call<ManageUser> call, Response<ManageUser> response) {
                ManageUser manageUser = response.body();
                updateUserFragment.getCallback(manageUser,String.valueOf(response.code()));
            }
            @Override
            public void onFailure(Call<ManageUser> call, Throwable throwable) {
                updateUserFragment.callbackFailed(throwable.getMessage());
            }
        });
    }
    public void getUserData(UpdateUserFragment  updateUserFragment){
        this.updateUserFragment = updateUserFragment;
        ApiService apiService = RetrofitInstance.getService();
        Call<UserResponse> call = apiService.getUser("2");
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse result = response.body();
                if (result != null && result.getUser() != null){
                    updateUserFragment.getUserCallback(String.valueOf(response.code()));
                } else updateUserFragment.userCallbackFailed(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable throwable) {
                updateUserFragment.userCallbackFailed(String.valueOf(throwable.getMessage()));
            }
        });
    }

    //database
    public void addUser(User user){
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> userDao.insert(user));
    }
    public void updateUser(User user){
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> userDao.update(user));
    }
    public void deleteUser(User user){

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> userDao.delete(user));
    }
    public void deleteAllUser(){

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(userDao::deleteAllUsers);
    }
    public void insertAllUser(List<User> newUsers){

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> userDao.insertAll(newUsers));
    }
    public LiveData<List<User>> getAllUsers(){
        return userDao.getAllUsers();
    }
    public void updateAll(List<User> users){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> userDao.updateAll(users));
    }
}

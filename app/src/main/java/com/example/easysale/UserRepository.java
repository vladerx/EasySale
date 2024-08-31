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

    private MutableLiveData<ManageUser> manageUser = new MutableLiveData<>();
    private MutableLiveData<UserResponse> userResponse = new MutableLiveData<>();
    private MutableLiveData<Result> result = new MutableLiveData<>();
    private final UserDao userDao;
    private final Application application;


    public UserRepository(Application application) {
        this.application = application;
        this.userDao = UserDatabase.getInstance(application).getUserDao();
    }
    //api

    public MutableLiveData<ManageUser> loginUser(String email, String password){
        ApiService apiService = RetrofitInstance.getService();
        Call<ManageUser> call = apiService.loginUser(email, password);
        call.enqueue(new Callback<ManageUser>() {
            @Override
            public void onResponse(Call<ManageUser> call, Response<ManageUser> response) {
                manageUser.setValue(response.body());
                Toast.makeText(application.getApplicationContext(), "Code: "+response.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ManageUser> call, Throwable throwable) {
                Toast.makeText(application.getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return manageUser;
    }
    public MutableLiveData<ManageUser> registerUser(String email, String password){
        ApiService apiService = RetrofitInstance.getService();
        Call<ManageUser> call = apiService.registerNewUser(email, password);
        call.enqueue(new Callback<ManageUser>() {
            @Override
            public void onResponse(Call<ManageUser> call, Response<ManageUser> response) {
                manageUser.setValue(response.body());
                Toast.makeText(application.getApplicationContext(), "Code: "+response.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ManageUser> call, Throwable throwable) {
                Toast.makeText(application.getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return manageUser;
    }

    public MutableLiveData<Result> petchUsersPerPage(int totalPages){
        ApiService apiService = RetrofitInstance.getService();
        Call<Result> call = apiService.getPageData(String.valueOf((totalPages+1)));
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                result.setValue(response.body());
                Toast.makeText(application.getApplicationContext(), "Code: "+response.code() + " page fetched!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable throwable) {
                Toast.makeText(application.getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return result;
    }
    public MutableLiveData<ManageUser> createUser(){
        ApiService apiService = RetrofitInstance.getService();
        Call<ManageUser> call = apiService.createUser("morpheus","leader");
        call.enqueue(new Callback<ManageUser>() {
            @Override
            public void onResponse(Call<ManageUser> call, Response<ManageUser> response) {
                manageUser.setValue(response.body());
                Toast.makeText(application.getApplicationContext(), "Code: "+response.code(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<ManageUser> call, Throwable throwable) {
                Toast.makeText(application.getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return manageUser;
    }
    public MutableLiveData<ManageUser> updateUser(){
        ApiService apiService = RetrofitInstance.getService();
        Call<ManageUser> call = apiService.updateUser("2","morpheus","zion resident");
        call.enqueue(new Callback<ManageUser>() {
            @Override
            public void onResponse(Call<ManageUser> call, Response<ManageUser> response) {
                manageUser.setValue(response.body());
                Toast.makeText(application.getApplicationContext(), "Code: "+response.code(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<ManageUser> call, Throwable throwable) {
                Toast.makeText(application.getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return manageUser;
    }
    public MutableLiveData<UserResponse> getUserData(){
        ApiService apiService = RetrofitInstance.getService();
        Call<UserResponse> call = apiService.getUser("2");
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                userResponse.setValue(response.body());
                Toast.makeText(application.getApplicationContext(), "Code: "+response.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable throwable) {
                Toast.makeText(application.getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return userResponse;
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

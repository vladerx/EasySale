package com.example.easysale;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.easysale.data.ManageUser;
import com.example.easysale.data.Result;
import com.example.easysale.data.User;
import com.example.easysale.data.UserResponse;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private UserRepository repository;

    public ViewModel(@NonNull Application application) {
        super(application);
        this.repository = new UserRepository(application);
    }

    //api
    public MutableLiveData<UserResponse> getUser(){
        return repository.getUserData();
    }
    public MutableLiveData<Result> petchUsersPerPage(int totalPages){
        return repository.petchUsersPerPage(totalPages);
    }
    public MutableLiveData<ManageUser> createUser(){
        return repository.createUser();
    }
    public MutableLiveData<ManageUser> registerUser(String email, String password){
        return repository.registerUser(email, password);
    }

    public MutableLiveData<ManageUser> loginUser(String email, String password){
       return repository.loginUser(email, password);
    }
    public MutableLiveData<ManageUser> updateUser(){
        return repository.updateUser();
    }

    //database
    public LiveData<List<User>> getAllUsersFromDB() {
        return repository.getAllUsers();
    }
    public void addNewUser(User user){
        repository.addUser(user);
    }
    public void updateUser(User user){
        repository.updateUser(user);
    }
    public void deleteUser(User user){
        repository.deleteUser(user);
    }
    public void deleteAllUsers(){
        repository.deleteAllUser();
    }
    public void insertAllUsers(List<User> users){
        repository.insertAllUser(users);
    }
    public void updateAllUsers(List<User> users){
        repository.updateAll(users);
    }
}

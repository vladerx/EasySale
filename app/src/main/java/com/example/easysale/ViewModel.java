package com.example.easysale;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.easysale.data.ManageUser;
import com.example.easysale.data.User;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private UserRepository repository;

    public ViewModel(@NonNull Application application) {
        super(application);
        this.repository = new UserRepository(application);
    }

    public void getUser(UpdateUserFragment  updateUserFragment){
        repository.getUserData(updateUserFragment);
    }

    public void petchUsersPerPage(UserListFragment userListFragment, int totalPages){
        repository.petchUsersPerPage(userListFragment,totalPages);
    }
    public void createUser(CreateUserFragment createUserFragment){
        repository.createUser(createUserFragment);
    }
    public void registerUser(String email, String password, LoginActivity loginActivity){
        repository.registerUser(email, password, loginActivity);
    }

    public void loginUser(String email, String password, LoginActivity loginActivity){
       repository.loginUser(email, password, loginActivity);
    }
    public void updateUser(UpdateUserFragment updateUserFragment){
        repository.updateUser(updateUserFragment);
    }

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

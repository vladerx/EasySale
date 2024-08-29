package com.example.easysale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.easysale.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding amb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amb = ActivityMainBinding.inflate(getLayoutInflater());
        View view = amb.getRoot();
        setContentView(view);

        SharedPreferences sharedPreferences = getSharedPreferences("saveData", Context.MODE_PRIVATE);
        int currentActivity = sharedPreferences.getInt("currentActivity", 0);
        int status = sharedPreferences.getInt("status", -1);

        if (currentActivity == 1){
            UpdateUserFragment updateUserFragment = new UpdateUserFragment();
            Bundle bundle = new Bundle();
            bundle.putString("clickedFirstName",sharedPreferences.getString("clickedFirstName",""));
            bundle.putString("clickedLastName",sharedPreferences.getString("clickedLastName",""));
            bundle.putString("clickedEmail",sharedPreferences.getString("clickedEmail",""));
            bundle.putInt("clickedId",sharedPreferences.getInt("clickedId", 0));
            bundle.putInt("clickedUserId",sharedPreferences.getInt("clickedUserId", 0));
            bundle.putString("clickedAvatar",sharedPreferences.getString("clickedAvatar",""));
            bundle.putString("clickedPage",sharedPreferences.getString("clickedPage",""));
            bundle.putInt("currentPage",sharedPreferences.getInt("currentPage", 0));
            bundle.putString("imageUri",sharedPreferences.getString("imageUri", ""));
            updateUserFragment.setArguments(bundle);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.recycContainLayout, updateUserFragment);
            ft.commit();
        } else if (currentActivity == 2){
            CreateUserFragment createUserFragment = new CreateUserFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("perPage",sharedPreferences.getInt("perPage", 6));
            bundle.putInt("totalPages",sharedPreferences.getInt("totalPages", 0));
            bundle.putInt("currentPage",sharedPreferences.getInt("currentPage", 0));
            bundle.putString("imageUri",sharedPreferences.getString("imageUri", ""));
            bundle.putString("firstName",sharedPreferences.getString("firstName", ""));
            bundle.putString("lastName",sharedPreferences.getString("lastName", ""));
            bundle.putString("email",sharedPreferences.getString("email", ""));
            createUserFragment.setArguments(bundle);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.recycContainLayout, createUserFragment);
            ft.commit();
        } else {
            UserListFragment userListFragment = new UserListFragment();

            if (status != -1) {
                Bundle bundle = new Bundle();
                bundle.putInt("totalPages", sharedPreferences.getInt("totalPages", 0));
                bundle.putInt("currentPage", sharedPreferences.getInt("currentPage", 1));
                bundle.putString("searchPhrase", sharedPreferences.getString("searchPhrase", ""));
                userListFragment.setArguments(bundle);
            }
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.recycContainLayout, userListFragment);
            ft.commit();
        }

    }
}
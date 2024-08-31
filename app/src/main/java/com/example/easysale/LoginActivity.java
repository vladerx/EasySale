package com.example.easysale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.easysale.api.ApiService;
import com.example.easysale.api.RetrofitInstance;
import com.example.easysale.data.ManageUser;
import com.example.easysale.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding alb;
    private boolean isRegisterClicked = false;
    private ViewModel viewModel;
    private Observer observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alb = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = alb.getRoot();
        setContentView(view);
        SharedPreferences sharedPreferences = getSharedPreferences("saveData", Context.MODE_PRIVATE);
        int status = sharedPreferences.getInt("currentActivity", -1);
        if (status > -1 && status < 3){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        } else {
            sharedPreferences.getString("emailLogin", "");
            sharedPreferences.getString("passwordLogin", "");
            isRegisterClicked = sharedPreferences.getInt("isRegister", 0) == 1;
            if (isRegisterClicked){
                alb.loginButton.setText("Register");
                alb.registerTextView.setVisibility(View.INVISIBLE);
            }
        }
        observer = (Observer<ManageUser>) manageUser -> {
            alb.loginProgressBar.setVisibility(View.INVISIBLE);
            if (manageUser != null){
                if (isRegisterClicked){
                    Toast.makeText(getApplicationContext(), "Succesful Registration!", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(), "Succesful Login!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Unsuccesful Login!", Toast.LENGTH_SHORT).show();
            }

        };

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        alb.registerTextView.setOnClickListener(click ->{
            isRegisterClicked = true;
            alb.loginButton.setText("Register");
            alb.registerTextView.setVisibility(View.INVISIBLE);
        });

        alb.loginButton.setOnClickListener( click ->{
            if (!alb.emailEditText.getText().toString().equals("") && !alb.passwordEditText.getText().toString().equals("")) {
                alb.loginProgressBar.setVisibility(View.VISIBLE);
                if (!isRegisterClicked)
                    viewModel.loginUser(alb.emailEditText.getText().toString(),alb.passwordEditText.getText().toString()).observe(this, observer);
                else viewModel.registerUser(alb.emailEditText.getText().toString(),alb.passwordEditText.getText().toString()).observe(this, observer);;
            } else {
                alb.loginProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "All Fields Required to Fill!", Toast.LENGTH_SHORT).show();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isRegisterClicked){
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_HOME);
                    startActivity(i);
                } else {
                    isRegisterClicked = false;
                    alb.loginButton.setText("Login");
                    alb.registerTextView.setVisibility(View.VISIBLE);
                    alb.loginProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("saveData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentActivity", 3);

        if (!alb.emailEditText.getText().toString().equals(""))
            editor.putString("emailLogin", alb.emailEditText.getText().toString());
        else editor.putString("emailLogin", "");

        if (!alb.passwordEditText.getText().toString().equals(""))
            editor.putString("passwordLogin", alb.passwordEditText.getText().toString());
        if (isRegisterClicked)
            editor.putInt("isRegister",1);
        else editor.putInt("isRegister",0);
        editor.apply();
    }

    private void resetApp(){
        this.deleteDatabase("user_db");
        SharedPreferences sharedPreferences = getSharedPreferences("saveData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("status", -1);
        editor.putInt("currentActivity", -1);
        editor.apply();
    }
}
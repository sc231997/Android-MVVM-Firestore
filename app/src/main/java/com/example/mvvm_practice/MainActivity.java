package com.example.mvvm_practice;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.mvvm_practice.Model.User;
import com.example.mvvm_practice.ViewModel.UserViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {
    TextView name_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name_tv = findViewById(R.id.name);

        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUsers().observe(this, users -> {
            for (User user: users.data()){
                name_tv.setText(user.getName());
            }
        });
    }
}

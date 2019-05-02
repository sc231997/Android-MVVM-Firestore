package com.example.mvvm_practice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.mvvm_practice.Model.User;
import com.example.mvvm_practice.ViewModel.UserViewModel;

public class MainActivity extends AppCompatActivity {
    TextView name_tv;
    Button player_activity_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUsers().observe(this, users -> {
            for (User user : users.data()) {
                name_tv.setText(user.getName());
            }
        });
    }

    private void init() {
        name_tv = findViewById(R.id.name);
        player_activity_btn = findViewById(R.id.btn_audio_player);

        player_activity_btn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,AudioPlayerActivity.class);
            startActivity(intent);
        });
    }
}

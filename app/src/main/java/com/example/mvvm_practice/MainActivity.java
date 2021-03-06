package com.example.mvvm_practice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.mvvm_practice.Model.User;
import com.example.mvvm_practice.ViewModel.UserViewModel;
import com.example.mvvm_practice.service.AudioPlayerService;

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
            Intent intent = new Intent(MainActivity.this, AudioPlayerActivity.class);
            startActivity(intent);
        });


        startAudioPlayerService();
    }

    private void startAudioPlayerService() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(intent, connection, BIND_AUTO_CREATE);
//        Util.startForegroundService(this,intent);
    }
}

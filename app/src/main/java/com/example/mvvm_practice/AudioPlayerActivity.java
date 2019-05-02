package com.example.mvvm_practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.example.mvvm_practice.service.AudioPlayerService;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;

public class AudioPlayerActivity extends AppCompatActivity {

    private PlayerControlView playerControlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        playerControlView = findViewById(R.id.playerView);
        startAudioService();
    }

    public void startAudioService() {
        ServiceConnection connection = new ServiceConnection() {
            private static final String TAG = "MainActivity";

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                AudioPlayerService.MyBinder binder = (AudioPlayerService.MyBinder)iBinder;
                ExoPlayer player = binder.getExoPlayer();
                playerControlView.setPlayer(player);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        Intent intent = new Intent(this, AudioPlayerService.class);
//        Util.startForegroundService(this, intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
}

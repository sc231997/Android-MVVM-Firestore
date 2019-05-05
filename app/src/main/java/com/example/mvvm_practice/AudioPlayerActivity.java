package com.example.mvvm_practice;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.mvvm_practice.Model.Post;
import com.example.mvvm_practice.ViewModel.AudioViewModel;
import com.google.android.exoplayer2.ui.PlayerControlView;

import java.util.ArrayList;

public class AudioPlayerActivity extends AppCompatActivity {

    ArrayList<Post> posts = new ArrayList<>();
    private PlayerControlView playerControlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        playerControlView = findViewById(R.id.playerView);

        AudioViewModel viewModel = ViewModelProviders.of(this).get(AudioViewModel.class);

        viewModel.getIsBound().observe(this, isBound -> {
            playerControlView.setPlayer(viewModel.getPlayer());
            viewModel.startAudioPlayerService();
        });
    }

}

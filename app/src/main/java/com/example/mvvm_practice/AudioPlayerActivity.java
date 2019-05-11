package com.example.mvvm_practice;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.mvvm_practice.ViewModel.AudioViewModel;
import com.google.android.exoplayer2.ui.PlayerControlView;

public class AudioPlayerActivity extends AppCompatActivity {

    private PlayerControlView playerControlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        playerControlView = findViewById(R.id.playerView);

        AudioViewModel viewModel = ViewModelProviders.of(this).get(AudioViewModel.class);
        viewModel.startAudioPlayerService();
        viewModel.getIsBound().observe(this, isBound -> {
            if (isBound) {
                playerControlView.setPlayer(viewModel.getPlayer());
                viewModel.getPosts().observe(this, posts ->
                        viewModel.startPlayer(posts.data(), 0));
            }
        });
    }

}

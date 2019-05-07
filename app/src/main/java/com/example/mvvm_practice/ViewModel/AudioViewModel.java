package com.example.mvvm_practice.ViewModel;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.mvvm_practice.Model.Post;
import com.example.mvvm_practice.service.AudioPlayerService;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;

import static android.content.Context.BIND_AUTO_CREATE;

public class AudioViewModel extends AndroidViewModel {
    private SimpleExoPlayer player;
    private Context context;
    private MutableLiveData<Boolean> isBound = new MutableLiveData<>();

    public AudioViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        isBound.setValue(false);
    }

    public MutableLiveData<Boolean> getIsBound() {
        return isBound;
    }

    public void startAudioPlayerService() {
        ArrayList<Post> posts = new ArrayList<>();

        Post post = new Post();
        post.setTitle("Sample 1");
        post.setImage("https://www.gstatic.com/webp/gallery3/2_webp_ll.webp");
        post.setArtist("Chandan");
        post.setAudio("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
        posts.add(post);

        Post post1 = new Post();
        post1.setTitle("Sample 2");
        post1.setImage("https://www.gstatic.com/webp/gallery3/1_webp_ll.webp");
        post1.setArtist("Singh");
        post1.setAudio("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3");
        posts.add(post1);

        Intent intent = new Intent(context, AudioPlayerService.class);
        intent.putExtra("POSTS", posts);

        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                AudioPlayerService.MyBinder binder = (AudioPlayerService.MyBinder) iBinder;
                player = binder.getPlayer();
                isBound.setValue(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        context.bindService(intent, connection, BIND_AUTO_CREATE);
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }
}

package com.example.mvvm_practice.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.mvvm_practice.AudioPlayerActivity;
import com.example.mvvm_practice.Model.Post;
import com.example.mvvm_practice.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.mvvm_practice.C.MEDIA_SESSION_TAG;
import static com.example.mvvm_practice.C.PLAYBACK_CHANNEL_ID;
import static com.example.mvvm_practice.C.PLAYBACK_NOTIFICATION_ID;

public class AudioPlayerService extends Service {
    final MyBinder iBinder = new MyBinder();
    SimpleExoPlayer player;
    PlayerNotificationManager playerNotificationManager;
    private MediaSessionCompat mediaSession;
    private MediaSessionConnector mediaSessionConnector;

    @Override
    public void onDestroy() {
        mediaSession.release();
        mediaSessionConnector.setPlayer(null, null);
        playerNotificationManager.setPlayer(null);

        player.release();
        player = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        final Context context = this;
        ArrayList<Post> posts = (ArrayList<Post>) intent.getSerializableExtra("POSTS");
        int index = intent.getExtras().getInt("INDEX", 0);

        player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        player.setPlayWhenReady(true);

        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, String.valueOf(R.string.app_name)));

        for (Post post : posts) {
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(post.getAudio()));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        player.prepare(concatenatingMediaSource);
        player.seekTo(index, C.TIME_UNSET);

        player.addListener(new Player.EventListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    // media actually playing
                    playerNotificationManager.setOngoing(true);
                    stopForeground(false);
                } else if (playWhenReady) {
                    // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)from
                    playerNotificationManager.setOngoing(true);
                    stopForeground(false);
                } else {
                    // player paused in any state
                    Log.e("Paused", "I was called");
                    stopForeground(true);
                    playerNotificationManager.setOngoing(false);
                    playerNotificationManager.setPriority(1);
                }
            }
        });

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                context, PLAYBACK_CHANNEL_ID, R.string.playback_channel_name, PLAYBACK_NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return posts.get(player.getCurrentWindowIndex()).getTitle();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intent = new Intent(context, AudioPlayerActivity.class);
                        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        return posts.get(player.getCurrentWindowIndex()).getArtist();
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {

                        Thread thread = new Thread(() -> {
                            try {
                                Bitmap bitmap = Glide.with(context)
                                        .asBitmap()
                                        .load(Uri.parse(posts.get(player.getCurrentWindowIndex()).getImage()))
                                        .submit().get();
                                callback.onBitmap(bitmap);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        thread.start();

                        return null;
                    }
                }
        );


        playerNotificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                startForeground(notificationId, notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                Log.e("Cancelled", "Notification Cancelled called");
                stopForeground(true);
            }
        });

        playerNotificationManager.setPlayer(player);

        mediaSession = new MediaSessionCompat(context, MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());

        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
                return new MediaDescriptionCompat.Builder()
                        .setMediaId(String.valueOf(posts.indexOf(posts.get(windowIndex))))
                        .setIconUri(Uri.parse(posts.get(windowIndex).getImage()))
                        .setTitle(posts.get(windowIndex).getTitle())
                        .setDescription(posts.get(windowIndex).getArtist())
                        .build();
            }
        });
        mediaSessionConnector.setPlayer(player, null);
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    public class MyBinder extends Binder {
        public SimpleExoPlayer getPlayer() {
            return player;
        }
    }
}

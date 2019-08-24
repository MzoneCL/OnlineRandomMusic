package com.example.onlinerandommusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.onlinerandommusic.listeners.OnNetworkMusicPreparedListener;

import java.io.IOException;

public class MusicPlayService extends Service {
    private static String TAG = "MusicPlayService";

    MediaPlayer mediaPlayer;

    boolean firstTimePlay;

    public MusicPlayService(){}

    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            firstTimePlay = true;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyMusicPlayBinder();
    }

    public class MyMusicPlayBinder extends Binder {
        public void playMusic(){
            if (!mediaPlayer.isPlaying())
                mediaPlayer.start();
        }

        public void pauseMusic(){
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
        }

        public void playRandomMusic(String url, final OnNetworkMusicPreparedListener onNetworkMusicPreparedListener) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();
                mediaPlayer.setLooping(true); // 循环播放
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        onNetworkMusicPreparedListener.onPrepared();
                        firstTimePlay = false;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isPlaying(){
            return mediaPlayer.isPlaying();
        }

        public int getMusicDuration(){
            return mediaPlayer.getDuration();
        }

        // 获取当前播放进度
        public int getCurPosition(){
            return mediaPlayer.getCurrentPosition();
        }

        public boolean isFirstTimePlay(){
            return firstTimePlay;
        }
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }
}

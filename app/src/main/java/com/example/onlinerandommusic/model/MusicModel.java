package com.example.onlinerandommusic.model;

import com.example.onlinerandommusic.listeners.GetRandomMusicListener;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MusicModel {
    public static void getRandomMusic(final GetRandomMusicListener getRandomMusicListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                Response response = null;
                Music music = null;

                Request request = new Request.Builder().get().url("https://api.comments.hk/").build();
                Call call = client.newCall(request);
                try {
                    response = call.execute();
                    String jsonString = response.body().string();
                    System.out.println(jsonString);
                    music = new Gson().fromJson(jsonString, Music.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (music != null){
                    getRandomMusicListener.onSuccess(music);
                }else
                    getRandomMusicListener.onFailed();
            }
        }).start();

    }
}

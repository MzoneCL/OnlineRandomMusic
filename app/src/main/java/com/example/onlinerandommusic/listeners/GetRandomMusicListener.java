package com.example.onlinerandommusic.listeners;

import com.example.onlinerandommusic.model.Music;

public interface GetRandomMusicListener {
    void onSuccess(Music music);

    void onFailed();
}

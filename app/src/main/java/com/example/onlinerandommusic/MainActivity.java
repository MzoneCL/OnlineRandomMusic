package com.example.onlinerandommusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinerandommusic.listeners.GetRandomMusicListener;
import com.example.onlinerandommusic.listeners.OnNetworkMusicPreparedListener;
import com.example.onlinerandommusic.model.Music;
import com.example.onlinerandommusic.model.MusicModel;
import com.example.onlinerandommusic.service.MusicPlayService;
import com.example.onlinerandommusic.util.LogUtil;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "MainActivity";

    private ImageButton btn_play_or_pause;
    private ImageButton btn_next_random;

    private TextView tv_music_title;

    private MusicPlayService.MyMusicPlayBinder musicController;

    private ServiceConnection serviceConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicController = (MusicPlayService.MyMusicPlayBinder) service;
            if (musicController.isPlaying())
                btn_play_or_pause.setImageResource(R.drawable.pause);
            else
                btn_play_or_pause.setImageResource(R.drawable.play);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initService();

        if (savedInstanceState != null)
            tv_music_title.setText(savedInstanceState.getString("musicTitle"));
    }



    private void initView(){
        btn_play_or_pause = (ImageButton) findViewById(R.id.btn_play_or_pause);
        btn_next_random = (ImageButton) findViewById(R.id.next_random);
        tv_music_title = (TextView) findViewById(R.id.tv_music_title);

        btn_play_or_pause.setOnClickListener(this);
        btn_next_random.setOnClickListener(this);
    }

    private void initService(){
        Intent intent = new Intent(this, MusicPlayService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play_or_pause:
                playOrPauseMusic();
                break;
            case R.id.next_random:
                nextRandomMusic();
            default:
                break;
        }
    }

    private void playMusic(){
        musicController.playMusic();
    }

    private void pauseMusic(){
        musicController.pauseMusic();
    }

    private void playOrPauseMusic(){

        if (musicController.isFirstTimePlay()){
            Toast.makeText(MainActivity.this, "没有正在播放的音乐！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (musicController.isPlaying()){
            pauseMusic();
            btn_play_or_pause.setImageResource(R.drawable.play);
        }else {
            playMusic();
            btn_play_or_pause.setImageResource(R.drawable.pause);
        }
    }

    private void nextRandomMusic(){
        btn_play_or_pause.setImageResource(R.drawable.play);
        MusicModel.getRandomMusic(new GetRandomMusicListener() {
            @Override
            public void onSuccess(final Music music) {
                LogUtil.e(TAG, music.getDescription());
                musicController.playRandomMusic(music.getMp3_url(), new OnNetworkMusicPreparedListener() {
                    @Override
                    public void onPrepared() {
                        btn_play_or_pause.setImageResource(R.drawable.pause);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_music_title.setText(music.getTitle());
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailed() {
                LogUtil.e(TAG, "获取音乐失败！");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    // 按返回键不退出
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("musicTitle", tv_music_title.getText().toString());
    }
}

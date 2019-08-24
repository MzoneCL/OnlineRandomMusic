package com.example.onlinerandommusic;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.onlinerandommusic.listeners.GetRandomMusicListener;
import com.example.onlinerandommusic.listeners.OnNetworkMusicPreparedListener;
import com.example.onlinerandommusic.model.Music;
import com.example.onlinerandommusic.model.MusicModel;
import com.example.onlinerandommusic.service.MusicPlayService;
import com.example.onlinerandommusic.util.TimeUtil;
import com.example.onlinerandommusic.util.LogUtil;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "MainActivity";
    private int IMAGE_SOURCE_PLAY = R.drawable.play_white;
    private int IMAGE_SOURCE_PAUSE = R.drawable.pause_white;

    private ImageButton btn_play_or_pause;
    private ImageButton btn_next_random;

    private ImageView image_view_music;
    private ImageView image_view_bg; // 背景图
    private ImageView image_view_user_avatar; // 热评用户头像

    private TextView tv_music_title;
    private TextView tv_music_desc;
    private TextView tv_comment;
    private TextView tv_comment_time;
    private TextView tv_comment_username;
    private TextView tv_liked_count;
    private TextView tv_music_total_time;
    private TextView tv_music_cur_time;

    private MusicPlayService.MyMusicPlayBinder musicController;

    ObjectAnimator objectAnimator; // 图片旋转动画
    SeekBar seekBar; // 进度条


    private ServiceConnection serviceConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicController = (MusicPlayService.MyMusicPlayBinder) service;
            if (musicController.isPlaying())
                btn_play_or_pause.setImageResource(IMAGE_SOURCE_PAUSE);
            else
                btn_play_or_pause.setImageResource(IMAGE_SOURCE_PLAY);
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
        tv_music_desc = (TextView) findViewById(R.id.tv_music_desc);
        image_view_music = (ImageView) findViewById(R.id.image_view_music);
        image_view_bg = (ImageView) findViewById(R.id.image_view_bg);
        tv_comment = (TextView) findViewById(R.id.tv_comment);
        image_view_user_avatar = (ImageView) findViewById(R.id.image_view_user_avatar);
        tv_comment_time = (TextView) findViewById(R.id.tv_comment_time);
        tv_comment_username = (TextView) findViewById(R.id.tv_comment_username);
        tv_liked_count = (TextView) findViewById(R.id.tv_liked_count);
        tv_music_total_time = (TextView) findViewById(R.id.tv_music_total_time);
        tv_music_cur_time = (TextView) findViewById(R.id.tv_music_cur_time);

        seekBar = (SeekBar) findViewById(R.id.music_progress_seek_bar);

        // 禁止拖动 点击
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        objectAnimator = ObjectAnimator.ofFloat(image_view_music, "rotation", 0, 360);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(20 * 1000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);//Animation.INFINITE 表示重复多次
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);//RESTART表示从头开始，REVERSE表示从末尾倒播

        btn_play_or_pause.setOnClickListener(this);
        btn_next_random.setOnClickListener(this);
    }

    private void initService(){
        Intent intent = new Intent(this, MusicPlayService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void playMusic(){
        musicController.playMusic();
        objectAnimator.resume();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void pauseMusic(){
        musicController.pauseMusic();
        objectAnimator.pause();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void playOrPauseMusic(){

        if (musicController.isFirstTimePlay()){
            Toast.makeText(MainActivity.this, "没有正在播放的音乐！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (musicController.isPlaying()){
            pauseMusic();
            btn_play_or_pause.setImageResource(IMAGE_SOURCE_PLAY);
        }else {
            playMusic();
            btn_play_or_pause.setImageResource(IMAGE_SOURCE_PAUSE);
        }
    }

    private void nextRandomMusic(){
        btn_play_or_pause.setImageResource(IMAGE_SOURCE_PLAY);
        MusicModel.getRandomMusic(new GetRandomMusicListener() {
            @Override
            public void onSuccess(final Music music) {
                LogUtil.e(TAG, music.getDescription());
                musicController.playRandomMusic(music.getMp3_url(), new OnNetworkMusicPreparedListener() {
                    @Override
                    public void onPrepared() {
                        btn_play_or_pause.setImageResource(IMAGE_SOURCE_PAUSE);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_music_title.setText(music.getTitle());
                                tv_music_desc.setText(music.getDescription().replaceAll("。", "  "));
                                tv_comment.setText(music.getComment_content());
                                tv_comment_time.setText(music.getComment_pub_date());
                                tv_comment_username.setText(music.getComment_nickname());
                                tv_liked_count.setText(music.getComment_liked_count() + "");

                                seekBar.setMax(musicController.getMusicDuration());
                                new UpdateProgressThread().start();
                                tv_music_total_time.setText(TimeUtil.format(musicController.getMusicDuration()));

                                Glide.with(MainActivity.this).load(music.getImages()).into(image_view_music);
                                Glide.with(MainActivity.this).load(music.getImages()).
                                        apply(RequestOptions.bitmapTransform(new BlurTransformation(50,10))).into(image_view_bg);
                                Glide.with(MainActivity.this).load(music.getComment_avatar_url()).placeholder(R.drawable.placeholder).into(image_view_user_avatar);
                                objectAnimator.start();
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

    class UpdateProgressThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (seekBar.getProgress() < seekBar.getMax()){
                final int curPosition = musicController.getCurPosition();
                seekBar.setProgress(curPosition);
                SystemClock.sleep(1000);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_music_cur_time.setText(TimeUtil.format(curPosition));
                    }
                });
            }
        }
    }
}

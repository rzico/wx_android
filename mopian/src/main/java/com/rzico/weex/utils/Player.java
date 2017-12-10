package com.rzico.weex.utils;

import java.io.IOException;  
import java.util.Timer;  
import java.util.TimerTask;  
import android.media.AudioManager;  
import android.media.MediaPlayer;  
import android.media.MediaPlayer.OnBufferingUpdateListener;  
import android.media.MediaPlayer.OnCompletionListener;  
import android.os.Handler;  
import android.os.Message;  
import android.util.Log;  
import android.widget.SeekBar;  
  
public class Player implements OnBufferingUpdateListener,  
        OnCompletionListener, MediaPlayer.OnPreparedListener{  
    public static MediaPlayer mediaPlayer;
    private Timer mTimer=new Timer();

    private static Player player = null;
    /* 1:懒汉式，静态工程方法，创建实例 */
    public static Player getInstance() {
        if (player == null || mediaPlayer == null) {
            player = new Player();
        }
        return player;
    }
    public Player()
    {
        try {  
            mediaPlayer = new MediaPlayer();  
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  
            mediaPlayer.setOnBufferingUpdateListener(this);  
            mediaPlayer.setOnPreparedListener(this);  
        } catch (Exception e) {  
            Log.e("mediaPlayer", "error", e);  
        }  
          
        mTimer.schedule(mTimerTask, 0, 1000);  
    }  
      
    /******************************************************* 
     * 通过定时器和Handler来更新进度条 
     ******************************************************/  
    TimerTask mTimerTask = new TimerTask() {  
        @Override  
        public void run() {  
            if(mediaPlayer==null)  
                return;
        }  
    };  

    //*****************************************************  
      
    public void play()
    {  
        mediaPlayer.start();  
    }  
      
    public void playUrl(String videoUrl)  
    {  
        try {  
            mediaPlayer.reset();  
            mediaPlayer.setDataSource(videoUrl);  
            mediaPlayer.prepare();//prepare之后自动播放  
            //mediaPlayer.start();  
        }  catch (Exception e){
            e.printStackTrace();
        }
    }  
  
      
    public void pause()  
    {  
        mediaPlayer.pause();  
    }  
      
    public void stop()  
    {  
        if (mediaPlayer != null) {   
            mediaPlayer.stop();  
            mediaPlayer.release();   
            mediaPlayer = null;
        }   
    }  
  
    @Override  
    /**  
     * 通过onPrepared播放  
     */  
    public void onPrepared(MediaPlayer arg0) {  
        arg0.start();  
        Log.e("mediaPlayer", "onPrepared");  
    }  
  
    @Override  
    public void onCompletion(MediaPlayer arg0) {  
        Log.e("mediaPlayer", "onCompletion");  
    }  
  
    @Override  
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
    }  
  
}  
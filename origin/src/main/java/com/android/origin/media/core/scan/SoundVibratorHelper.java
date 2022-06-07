package com.android.origin.media.core.scan;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.VibrationAttributes;
import android.os.Vibrator;

import com.android.origin.R;


/**
 * Created by you on 2018-04-26.
 * 扫描成功时的声音震动操作
 */
public final class SoundVibratorHelper {
    /**
     * 振动时间
     */
    private static final long DEF_VIBRATE_DURATION = 200L;

    private SoundPool soundPool;

    private Boolean isBeep = true;

    private int soundId;
    /**
     * 振动
     */
    private Vibrator vibrator;

    public SoundVibratorHelper(Context context) {
        context = context.getApplicationContext();
        AudioManager audioService = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            isBeep = false;
        }
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(2);
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_NOTIFICATION);
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();
        soundId = soundPool.load(context, R.raw.beep, 1);
    }

    public void play() {
       if (isBeep){
           soundPool.play(soundId, 1, 1, 0, 0, 1);
       }else{
           vibrator.vibrate(DEF_VIBRATE_DURATION);
       }
    }

    public void stop() {
        soundPool.release();
        vibrator.cancel();
    }

}

package com.android.origin

import android.media.AudioFormat
import android.media.MediaCodecInfo

/**
Created by Zebra-RD张先杰 on 2022年3月22日17:26:46

Description:
 */
object Constant {

    // ----------------- H264 -----------------
    const val FRAME_RATE = 20 //Camera中一般支持7~30

    const val IFRAME_INTERVAL: Int = 10 //关键帧间隔


    //相机的默认最小与最大帧率参数, 包含 FRAME_RATE 范围
    const val DEF_MIN_FPS = 15000
    const val DEF_MAX_FPS = 25000

    //扫描类的可以适当帧率高一些
    const val SCAN_MIN_FPS = 2500
    const val SCAN_MAX_FPS = 3000

    /**
     * 码率系数, w * h * 3, 此参数越大拍出的视频质量越大,最好不超过FRAME_RATE
     */
    const val VIDEO_BITRATE_COEFFICIENT = 3

    //----------------- Audio -----------------
    //双声道
    const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO
    const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

    const val SAMPLE_RATE = 44100

    //两声道对应的channelConfig为AudioFormat.CHANNEL_OUT_STEREO
    const val CHANNEL_COUNT = 2
    const val AUDIO_RATE = 1000 shl 6
    const val AAC_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectLC
}
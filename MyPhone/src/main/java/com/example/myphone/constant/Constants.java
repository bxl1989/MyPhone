package com.example.myphone.constant;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;

import static java.lang.Math.max;

/**
 * Created by bxl on 9/26/13.
 */
public class Constants {
    public static final int AUDIO_SAMPLE_RATE = 8000;
    public static final int AUDIO_ENCODE = AudioFormat.ENCODING_PCM_16BIT;
    public static final int AUDIO_IN_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    public static final int AUDIO_OUT_CHANNEL = AudioFormat.CHANNEL_OUT_MONO;
    public static final int AUDIO_OUT_BUF_LEN = AudioTrack.getMinBufferSize(Constants.AUDIO_SAMPLE_RATE, Constants.AUDIO_OUT_CHANNEL, Constants.AUDIO_ENCODE);
    public static final int AUDIO_IN_BUF_LEN = AudioRecord.getMinBufferSize(Constants.AUDIO_SAMPLE_RATE, Constants.AUDIO_IN_CHANNEL, Constants.AUDIO_ENCODE);
    public static final int SELF_BUF_LEN = max(AUDIO_IN_BUF_LEN, AUDIO_OUT_BUF_LEN);
    public static final int CHUNCK_LEN = 256;

    public static final int DATA_SERVER_PORT = 9999;
    public static final int KEY_SERVER_PORT = 8888;
    public static final int CMD_SERVER_PORT = 7777;

    public static final int FREE = 0, CALLING = 1, INCALL = 2, CALLDONE=3;
}

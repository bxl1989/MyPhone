package com.example.myphone.audio;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import com.example.myphone.constant.Constants;

import java.util.List;

/**
 * Created by bxl on 9/27/13.
 */
public class TrackManager extends Thread {

    private AudioTrack audioTrack = null;
    private boolean isTracking;
    private List<byte[]> audioList;
    private boolean isTerminate;
    private int bufLen;

    public TrackManager(List<byte[]> audioList, int bufLen) {
        this.audioList = audioList;
        this.bufLen = bufLen;
        isTracking = false;
        isTerminate = false;
    }

    @Override
    public void run() {
        super.run();
        byte[] buf;
        int audioWrite = 0;

        audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, Constants.AUDIO_SAMPLE_RATE, Constants.AUDIO_OUT_CHANNEL, Constants.AUDIO_ENCODE, bufLen, AudioTrack.MODE_STREAM);
        Log.d("outbuflen:", String.valueOf(Constants.AUDIO_OUT_BUF_LEN));
        if(audioTrack!=null){
            audioTrack.play();
            while(!isTerminate){
                if((isTracking && audioList.size()>5) || ((!isTracking && audioList.size()>0 && audioList.size()<=5) )){
                //if(isTracking && audioList.size()>0){
                    buf = audioList.remove(0);
                    audioWrite = audioTrack.write(buf, 0, buf.length);
                    Log.d("audioWrite", String.valueOf(audioWrite));
                    Log.d("trackAudioList", String.valueOf(audioList.size()));
                }
            }

        }
    }

    public void go(){
        isTracking = true;
    }

    public void pause(){
        isTracking = false;
    }

    public void terminate(){
        isTerminate = true;
        isTracking = false;
        if(audioTrack != null){
            audioTrack.stop();
            audioTrack.release();
        }
    }


}

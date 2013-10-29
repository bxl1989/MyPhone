package com.example.myphone.audio;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import com.example.myphone.constant.Constants;

import java.util.List;

/**
 * Created by bxl on 9/26/13.
 */
public class RecordManager extends Thread {


    private AudioRecord audioRecord = null;
    private boolean isRecording;
    private List<byte[]> audioList;
    private boolean isTerminate;
    private int bufLen, chunckLen;

    public RecordManager(List<byte[]> audioList, int bufLen, int chunckLen) {
        this.audioList = audioList;
        this.bufLen = bufLen;
        this.chunckLen = chunckLen;
        isRecording = false;
        isTerminate = false;
    }

    @Override
    public void run() {
        super.run();
        byte[] chunck;
        int audioRead;
        int length;
        int off;

        Log.d("inbuflen:", String.valueOf(Constants.AUDIO_IN_BUF_LEN));
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, Constants.AUDIO_SAMPLE_RATE, Constants.AUDIO_IN_CHANNEL, Constants.AUDIO_ENCODE, bufLen);
        Log.d("audioRecord state:", String.valueOf(audioRecord.getState()));
        if(audioRecord!=null){
            audioRecord.startRecording();
            while(!isTerminate){
                if(isRecording){
                    chunck = new byte[chunckLen];
                    length = chunckLen;
                    off=0;
                    while(length>0){
                        audioRead = audioRecord.read(chunck, off, length);
                        if(audioRead>0){
                            length-=audioRead;
                            off+=audioRead;
                            Log.d("audioRead", String.valueOf(audioRead));
                        }
                    }
                    audioList.add(chunck);
                    Log.d("recordAudioList", String.valueOf(audioList.size()));

                }
            }
        }
    }

    public void go(){
        isRecording = true;
    }

    public void pause(){
        isRecording = false;
    }

    public void terminate(){
        isTerminate = true;
        isRecording = false;
        if(audioRecord!=null){
            audioRecord.stop();
            audioRecord.release();
        }
    }
}

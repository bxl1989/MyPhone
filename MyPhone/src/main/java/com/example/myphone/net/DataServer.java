package com.example.myphone.net;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by bxl on 9/27/13.
 */
public class DataServer extends Thread {

    private List<byte[]> recvDataList;
    private InputStream socketInputStream;
    private byte[] dataBuf;
    private boolean isDone = false;
    private int bufLen;
    private boolean isTerminate = false;

    public DataServer(InputStream socketInputStream, List<byte[]> recvDataList, int bufLen) {
        this.socketInputStream = socketInputStream;
        this.recvDataList = recvDataList;
        this.bufLen = bufLen;
        isDone = false;
    }

    public boolean isDone() {
        return isDone;
    }

    @Override
    public void run() {
        super.run();
        int dataRead;
        try {
            if(socketInputStream!=null){
                dataBuf = new byte[bufLen];
                while (!isTerminate && ((dataRead = socketInputStream.read(dataBuf))!=-1)){
                    recvDataList.add(dataBuf);
                    dataBuf = new byte[bufLen];
                    Log.d("dataRead", String.valueOf(dataRead));
                    Log.d("recvDataList", String.valueOf(recvDataList.size()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        isDone = true;
    }

    public void terminate(){
        isTerminate = true;
    }
}

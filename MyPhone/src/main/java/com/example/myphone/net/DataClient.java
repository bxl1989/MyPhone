package com.example.myphone.net;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by bxl on 9/27/13.
 */
public class DataClient extends Thread {
    private List<byte[]> sendDataList;
    OutputStream socketOutputStream;
    byte[] dataBuf;
    boolean isterminate;


    public DataClient(OutputStream socketOutputStream, List<byte[]> sendDataList){
        isterminate = false;
        this.socketOutputStream = socketOutputStream;
        this.sendDataList = sendDataList;
    }

    @Override
    public void run() {
        super.run();
        try {
            if(socketOutputStream!=null){
                while(!isterminate){
                    if(sendDataList.size()>0){
                        dataBuf = sendDataList.remove(0);
                        socketOutputStream.write(dataBuf);
                        Log.d("sendDataList", String.valueOf(sendDataList.size()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void terminate(){
        isterminate = true;
    }
}

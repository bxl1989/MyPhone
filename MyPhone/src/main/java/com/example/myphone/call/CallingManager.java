package com.example.myphone.call;

import android.os.Handler;
import android.util.Log;
import com.example.myphone.audio.RecordManager;
import com.example.myphone.audio.TrackManager;
import com.example.myphone.constant.Constants;
import com.example.myphone.crypto.DecryptManager;
import com.example.myphone.crypto.EncryptManger;
import com.example.myphone.net.DataClient;
import com.example.myphone.net.DataServer;
import com.example.myphone.net.KeyClient;
import com.example.myphone.ui.MyPhoneActivity;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.max;

/**
 * Created by bxl on 10/8/13.
 */
public class CallingManager extends Thread {

    private String dstName;
    private boolean isTerminate;
    private TrackManager trackManager;
    private RecordManager recordManager;
    private EncryptManger encryptManger;
    private DecryptManager decryptManager;
    private DataServer dataServer;
    private DataClient dataClient;
    private List<byte[]> inAudioList;
    private List<byte[]> inDataList;
    private List<byte[]> outAudioList;
    private List<byte[]> outDataList;
    private Socket socket = null;
    private SecretKey key;
    private boolean isCrypto = true;
    private Handler uiHandler;

    public CallingManager(String dstName, Handler uiHandler, boolean isCrypto) {
        this.dstName = dstName;
        this.uiHandler = uiHandler;
        this.isCrypto = isCrypto;
        isTerminate = false;
    }

    @Override
    public void run() {
        super.run();
        try {
            socket = new Socket(dstName, Constants.DATA_SERVER_PORT);
            if(socket!=null){
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF("REQ");
                String result = inputStream.readUTF();
                if (result.equals("OK")){

                    outputStream.writeBoolean(isCrypto);

                    outputStream.writeInt(Constants.SELF_BUF_LEN);
                    int selfBufLen = Constants.SELF_BUF_LEN;
                    int targetBufLen = inputStream.readInt();
                    int bufLen = max(Constants.SELF_BUF_LEN, targetBufLen);
                    Log.d("bufLen", String.valueOf(bufLen));

                    inAudioList =  Collections.synchronizedList(new ArrayList<byte[]>());
                    inDataList = Collections.synchronizedList(new ArrayList<byte[]>());
                    outAudioList = Collections.synchronizedList(new ArrayList<byte[]>());
                    outDataList = Collections.synchronizedList(new ArrayList<byte[]>());
                    if(isCrypto){
                        recordManager = new RecordManager(inAudioList, bufLen, Constants.CHUNCK_LEN);
                        trackManager = new TrackManager(outAudioList, bufLen);
                        recordManager.start();
                        trackManager.start();
                        KeyClient keyClient = new KeyClient(socket);
                        keyClient.run();
                        key = keyClient.getKey();
                    }else{
                        recordManager = new RecordManager(outDataList, bufLen, Constants.CHUNCK_LEN);
                        trackManager = new TrackManager(inDataList, bufLen);
                        recordManager.start();
                        trackManager.start();
                    }
                    //BufferedInputStream socketInputStream = new BufferedInputStream(socket.getInputStream(), bufLen*5);
                    //BufferedOutputStream socketOutputStream = new BufferedOutputStream(socket.getOutputStream(), bufLen*5);

                    InputStream socketInputStream = socket.getInputStream();
                    OutputStream socketOutputStream = socket.getOutputStream();

                    dataServer = new DataServer(socketInputStream, inDataList, Constants.CHUNCK_LEN);
                    dataClient = new DataClient(socketOutputStream, outDataList);
                    dataServer.start();
                    dataClient.start();
                    if(isCrypto){
                        encryptManger = new EncryptManger(key, inAudioList, outDataList);
                        decryptManager = new DecryptManager(key, outAudioList, inDataList);
                        encryptManger.start();
                        decryptManager.start();
                    }

                    recordManager.go();
                    trackManager.go();

                    MyPhoneActivity.callState = Constants.INCALL;
                    uiHandler.sendEmptyMessage(Constants.INCALL);

                    while (!isTerminate && !dataServer.isDone()){
                        /*
                        Log.d("outDataList", String.valueOf(outDataList.size()));
                        Log.d("inAudioList", String.valueOf(inAudioList.size()));
                        Log.d("inDataList", String.valueOf(inDataList.size()));
                        Log.d("outAudioList", String.valueOf(outAudioList.size()));
                        */
                    }

                    dataServer.terminate();
                    dataClient.terminate();
                    recordManager.terminate();
                    trackManager.terminate();
                    if(isCrypto){
                        encryptManger.terminate();
                        decryptManager.terminate();
                    }

                    //inputStream.close();
                    //outputStream.close();
                    socket.close();

                    MyPhoneActivity.callState = Constants.FREE;
                    uiHandler.sendEmptyMessage(Constants.FREE);
                }else{
                    MyPhoneActivity.callState = Constants.FREE;
                    uiHandler.sendEmptyMessage(Constants.FREE);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            MyPhoneActivity.callState = Constants.FREE;
            uiHandler.sendEmptyMessage(Constants.FREE);
        }

    }

    public void terminate(){
        isTerminate = true;
    }
}

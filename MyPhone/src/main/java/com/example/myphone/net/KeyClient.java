package com.example.myphone.net;

import android.util.Log;
import com.example.myphone.ui.MyPhoneActivity;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * Created by bxl on 9/27/13.
 */
public class KeyClient {
    private Socket socket = null;
    DataInputStream socketInputStream;
    DataOutputStream socketOutputStream;
    int aliceL;
    byte[] alice, bob;
    SecretKey key;

    boolean isDone;

    public boolean isDone() {
        return isDone;
    }

    public SecretKey getKey() {
        return key;
    }

    public KeyClient(Socket socket) {
        this.socket = socket;
        isDone = false;
        key = null;
    }

    public void run() {
        try {
            if(socket!=null){
                socketOutputStream = new DataOutputStream(socket.getOutputStream());
                socketInputStream = new DataInputStream(socket.getInputStream());

                socketOutputStream.writeUTF("REQ");
                String reqResult = socketInputStream.readUTF();
                if(reqResult.equals("OK")){

                    synchronized (MyPhoneActivity.keyLock){
                        while(!MyPhoneActivity.isKeyDone){
                            MyPhoneActivity.keyLock.wait();
                        }
                    }



                    byte[] aliceGBytes, alicePBytes;
                    aliceGBytes = MyPhoneActivity.aliceG.toByteArray();
                    alicePBytes = MyPhoneActivity.aliceP.toByteArray();
                    socketOutputStream.writeInt(aliceGBytes.length);
                    socketOutputStream.write(aliceGBytes);
                    socketOutputStream.writeInt(alicePBytes.length);
                    socketOutputStream.write(alicePBytes);
                    //socketOutputStream.writeInt(aliceL);
                    socketOutputStream.writeInt(MyPhoneActivity.alice.length);
                    socketOutputStream.write(MyPhoneActivity.alice);

                    int bobLen = socketInputStream.readInt();
                    bob = new byte[bobLen];
                    socketInputStream.read(bob);

                    KeyGen();

                    socketOutputStream.writeUTF("DONE");
                    String doneResult = socketInputStream.readUTF();
                    if(doneResult.equals("DONE")){
                        isDone = true;
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void KeyGen(){
        try {
            KeyFactory kf = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(bob);
            PublicKey pk = kf.generatePublic(x509Spec);
            MyPhoneActivity.ka.doPhase(pk, true);
            byte[] secret = MyPhoneActivity.ka.generateSecret();
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            DESKeySpec desSpec = new DESKeySpec(secret);
            key = skf.generateSecret(desSpec);
            Log.d("secret", Arrays.toString(secret));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }
}

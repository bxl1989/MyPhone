package com.example.myphone.net;

import android.util.Log;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DHParameterSpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * Created by bxl on 9/27/13.
 */
public class KeyServer{
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private int port;
    DataInputStream socketInputStream;
    DataOutputStream socketOutputStream;
    BigInteger aliceP, aliceG;
    int aliceL;
    byte[] alice, bob;
    SecretKey key;
    KeyAgreement ka;
    boolean isDone;


    public KeyServer(Socket socket) {
        this.socket = socket;
        isDone = false;
    }


    public SecretKey getKey() {
        return key;
    }

    public boolean isDone() {
        return isDone;
    }

    public void run() {
        try {

            socketInputStream = new DataInputStream(socket.getInputStream());
            socketOutputStream = new DataOutputStream(socket.getOutputStream());

            String req = socketInputStream.readUTF();
            if(req.equals("REQ")){
                socketOutputStream.writeUTF("OK");
                byte[] aliceGBytes, alicePBytes;
                int aliceGBytesLen, alicePBytesLen;
                aliceGBytesLen = socketInputStream.readInt();
                aliceGBytes = new byte[aliceGBytesLen];
                socketInputStream.read(aliceGBytes);
                alicePBytesLen = socketInputStream.readInt();
                alicePBytes = new byte[alicePBytesLen];
                socketInputStream.read(alicePBytes);
                //aliceL = socketInputStream.readInt();
                int aliceLen = socketInputStream.readInt();
                alice = new byte[aliceLen];
                socketInputStream.read(alice);
                aliceG = new BigInteger(aliceGBytes);
                aliceP = new BigInteger(alicePBytes);

                KeyGenPhase1();

                socketOutputStream.writeInt(bob.length);
                socketOutputStream.write(bob);

                KeyGenPhase2();

                socketOutputStream.writeUTF("DONE");
                String doneResult = socketInputStream.readUTF();
                if(doneResult.equals("DONE")){
                    isDone = true;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void KeyGenPhase1(){
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            //DHParameterSpec dhSpec = new DHParameterSpec(aliceP, aliceG, aliceL);
            DHParameterSpec dhSpec = new DHParameterSpec(aliceP, aliceG);
            kpg.initialize(dhSpec);
            KeyPair kp = kpg.generateKeyPair();
            bob = kp.getPublic().getEncoded();
            ka = KeyAgreement.getInstance("DH");
            ka.init(kp.getPrivate());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }


    }

    private void KeyGenPhase2(){
        try {
            KeyFactory kf = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(alice);
            PublicKey pk = kf.generatePublic(x509Spec);
            ka.doPhase(pk, true);
            byte secret[] = ka.generateSecret();
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

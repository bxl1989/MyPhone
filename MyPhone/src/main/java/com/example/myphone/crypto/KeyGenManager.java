package com.example.myphone.crypto;

import com.example.myphone.ui.MyPhoneActivity;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bxl on 9/30/13.
 */
public class KeyGenManager extends Thread {

    @Override
    public void run() {
        super.run();
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(256);
            KeyPair kp = kpg.generateKeyPair();
            Class dhClass = Class.forName("javax.crypto.spec.DHParameterSpec");
            DHParameterSpec dhSpec = ((DHPublicKey)kp.getPublic()).getParams();
            MyPhoneActivity.aliceG = dhSpec.getG();
            MyPhoneActivity.aliceP = dhSpec.getP();
            //MyPhoneActivity.aliceL = dhSpec.getL();
            MyPhoneActivity.alice = kp.getPublic().getEncoded();
            MyPhoneActivity.ka = KeyAgreement.getInstance("DH");
            MyPhoneActivity.ka.init(kp.getPrivate());
            MyPhoneActivity.isKeyDone = true;
            synchronized (MyPhoneActivity.keyLock){
                MyPhoneActivity.keyLock.notify();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }
}

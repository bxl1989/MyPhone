package com.example.myphone.crypto;

import android.util.Log;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bxl on 9/26/13.
 */
public class EncryptManger extends Thread {
    SecretKey key;
    private List<byte[]> plainDataList, cipherDataList;
    private  boolean isterminate;
    Cipher cipher;
    int count=0;

    public EncryptManger(SecretKey key, List<byte[]> plainDataList, List<byte[]> cipherDataList) {
        this.key = key;
        this.plainDataList = plainDataList;
        this.cipherDataList = cipherDataList;
        isterminate = false;
        cipher = null;
    }

    @Override
    public void run() {
        super.run();
        try {
            //cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            while(!isterminate){
                if(plainDataList.size()>0){
                    Log.d("EnPlainText"+String.valueOf(count), Arrays.toString(plainDataList.get(0)));
                    count++;
                    Log.d("Encrypt PlainText Size", String.valueOf(plainDataList.get(0).length));
                    byte[] buf = cipher.doFinal(plainDataList.remove(0));
                    Log.d("Encrypt CipherText Size", String.valueOf(buf.length));
                    cipherDataList.add(buf);
                    Log.d("EnPlainDataList", String.valueOf(plainDataList.size()));
                    Log.d("EnCipherDataList", String.valueOf(cipherDataList.size()));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }

    public void terminate(){
        isterminate = true;
    }

}

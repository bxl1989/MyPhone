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
public class DecryptManager extends Thread {
    SecretKey key;
    private List<byte[]> plainDataList, cipherDataList;
    private  boolean isTerminate;
    Cipher cipher;
    int count=0;

    public DecryptManager(SecretKey key, List<byte[]> plainDataList, List<byte[]> cipherDataList) {
        this.key = key;
        this.plainDataList = plainDataList;
        this.cipherDataList = cipherDataList;
        isTerminate = false;
    }

    @Override
    public void run() {
        super.run();
        try {
            //cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            while(!isTerminate){
                if(cipherDataList.size()>0){
                    Log.d("Decrypt CipherText Size", String.valueOf(cipherDataList.get(0).length));
                    byte[] buf = cipher.doFinal(cipherDataList.remove(0));
                    Log.d("Decrypt PlainText Size", String.valueOf(buf.length));
                    Log.d("DePlainText"+String.valueOf(count), Arrays.toString(buf));
                    count++;
                    plainDataList.add(buf);
                    Log.d("DePlainDataList", String.valueOf(plainDataList.size()));
                    Log.d("DeCipherDataList", String.valueOf(cipherDataList.size()));
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
        isTerminate = true;
    }
}

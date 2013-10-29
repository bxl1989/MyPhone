package com.example.myphone.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.myphone.R;
import com.example.myphone.call.CalledManager;
import com.example.myphone.call.CallingManager;
import com.example.myphone.constant.Constants;
import com.example.myphone.crypto.KeyGenManager;

import javax.crypto.KeyAgreement;
import java.math.BigInteger;

public class MyPhoneActivity extends Activity {

    public static int callState;
    public static BigInteger aliceP, aliceG;
    public static byte[] alice;
    public static boolean isKeyDone = false;
    public static final  Object keyLock = new Object();
    public static KeyAgreement ka;
    CallingManager callingManager;
    CalledManager calledManager;
    private Handler handler;
    private Button btn_call;
    private EditText edit_dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        terminate();
    }

    public void init(){
        uiInit();
        bgInit();
    }

    public void uiInit(){
        btn_call = (Button) findViewById(R.id.btn_call);
        edit_dest = (EditText) findViewById(R.id.edit_dest);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Constants.CALLING:
                        edit_dest.setEnabled(false);
                        btn_call.setText("WAIT");
                        btn_call.setEnabled(false);
                        break;
                    case Constants.INCALL:
                        edit_dest.setEnabled(false);
                        btn_call.setText("CANCEL");
                        btn_call.setEnabled(true);
                        break;
                    case Constants.FREE:
                        edit_dest.setEnabled(true);
                        btn_call.setText("CALL");
                        btn_call.setEnabled(true);
                        break;
                    case Constants.CALLDONE:
                        edit_dest.setEnabled(false);
                        btn_call.setText("WAIT");
                        btn_call.setEnabled(false);
                        break;

                }
            }
        };
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_call.getText().equals("CALL")){
                    if(callState == Constants.FREE){
                        if(!edit_dest.getText().toString().equals("")){
                            Log.d("dstName", edit_dest.getText().toString());
                            AlertDialog.Builder builder = new AlertDialog.Builder(MyPhoneActivity.this);
                            builder.setTitle("CALLTO").setMessage(edit_dest.getText().toString());
                            builder.setPositiveButton("SECURE CALL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    callStart(true);
                                }
                            });
                            builder.setNegativeButton("NORMAL CALL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    callStart(false);
                                }
                            });
                            builder.show();


                        }
                    }
                }else{
                    if(callingManager!=null){
                        callingManager.terminate();
                        callingManager = null;
                    }
                    if(calledManager!=null){
                        calledManager.finish();
                    }
                    callState = Constants.CALLDONE;
                    handler.sendEmptyMessage(Constants.CALLDONE);
                    //btn_call.setText("CALL");

                }
            }
        });
    }

    public void bgInit(){
        //Intent intent = new Intent(this, CalledService.class);
        //startService(intent);
        calledManager = new CalledManager(handler);
        calledManager.start();
        if(!isKeyDone){
            KeyGenManager keyGenManager = new KeyGenManager();
            keyGenManager.start();
        }
    }

    public void terminate(){
        if(callingManager!=null){
            callingManager.terminate();
            callingManager = null;
        }
        if(calledManager!=null){
            calledManager.terminate();
            calledManager = null;
        }
        callState = Constants.FREE;
    }

    public void callStart(boolean isCrypto){
        callingManager = new CallingManager(edit_dest.getText().toString(), handler, isCrypto);
        callingManager.start();
        callState = Constants.CALLING;
        handler.sendEmptyMessage(Constants.CALLING);
    }


}

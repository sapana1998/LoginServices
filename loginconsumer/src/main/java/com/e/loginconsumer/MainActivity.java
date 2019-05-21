package com.e.loginconsumer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Messenger mService = null; //Messenger for communicating with the service
    boolean mBound; //Flag indicating whether we have called bind on the service

    Calendar c;
    SimpleDateFormat df = new SimpleDateFormat("(MMM.d.yyyy hh:mm:ss a)");//a for auto sec
    String formattedDate;
    String sendInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Class for interacting with the main interface of the service.
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };


    public void btnSend(View view) {
        c = Calendar.getInstance();
        formattedDate = df.format(c.getTime());

        EditText txtBoxInput = findViewById(R.id.txtInput);
        //Objects is parent class of all classes
        if (!Objects.equals(txtBoxInput.getText().toString(), "")) {
            sendInput = formattedDate + " " + txtBoxInput.getText().toString().replaceAll("\\n", "");

            if (!mBound) return;

            //Create and send a message to the service, using a supported 'what' value
            Bundle bundle = new Bundle();
            bundle.putString("msg", sendInput);

            Message msg = Message.obtain(null, 1, bundle);//0, 0);
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
        Intent intnt = new Intent("com.e.loginservices.logService");
        intnt.setPackage("com.e.loginservices");
        bindService(intnt, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
}

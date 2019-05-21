package com.e.loginservices;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainService extends Service {

     public MainService()
    {

    }

    //(Symbolic Constant)Constant for file for current service
    final static int SAVE_TO_FILE=1;
     //String which is going to be written in file for current
    String passedString;
    String timeStampedInput;

    /*
    There are two main uses for a Handler: (1) to schedule messages and
 * runnables to be executed as some point in the future
 * (2) to enqueue an action to be performed on a different thread than your own.
 * */

    class IncomingHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case SAVE_TO_FILE:
                    Bundle bundle=(Bundle) msg.obj;
                    passedString=bundle.getString("msg");
                    SaveInputToFile(passedString);
                    Toast.makeText(getApplicationContext(),passedString, Toast.LENGTH_SHORT).show();
                                    break;
                    default:
                        super.handleMessage(msg);
            }

        }

        public void SaveInputToFile(String timeStamedInput) {
            String filename = "LoggingServiceLog.txt";
            File externalFile;

            externalFile = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), filename);
            try {
                FileOutputStream fOutput = new FileOutputStream(externalFile, true);
                OutputStreamWriter wOutput = new OutputStreamWriter(fOutput);
                wOutput.append(timeStampedInput + "\n");
                wOutput.flush();    //clean the text whatever we write
                wOutput.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //Target we publish for clients to send messages to IncomingHandler.
    final Messenger mMessenger=new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent)//IBinder is communication between services and Activity
    {
        return mMessenger.getBinder();
    }
}
//3 types of permission user level,group level,other level
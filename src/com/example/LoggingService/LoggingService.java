package com.example.LoggingService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Service that acts like a system wide logging service for any application.
 * It records a timestamp with a specified message to an internal log file.
 * @author Madison Liddell
 * @since 11/10/2015
 */
public class LoggingService extends Service
{
    public static final int LOG_MESSAGE = 0;
    public static final String MESSAGE = "message";

    private static final String logFileName = "log.txt";
    private static Context context;
    private static BroadcastReceiver receiver;

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger messenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent)
    {
        return messenger.getBinder();
    }

    /**
     * Handler of incoming messages from clients.
     */
    static class IncomingHandler extends Handler
    {
        /**
         * Get the message to be logged, add timestamp, and then log it to the file.
         * @param msg message to be logged
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case LOG_MESSAGE:
                    // create the log entry
                    String entry = constructEntry(msg);
                    // log it to the file
                    logEntry(entry);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Creates log entry using timestamp and data from Message
     * @param msg contains bundle with the message to log
     * @return completed message
     */
    static private String constructEntry(Message msg)
    {
        // record timestamp
        long currentTime = System.currentTimeMillis();
        // convert long time to timestamp format
        SimpleDateFormat sdf = new SimpleDateFormat("MM-d-yyyy H:m:s");
        String timeStamp = sdf.format(currentTime);

        // construct log entry from timestamp + received message
        Bundle bundle = (Bundle) msg.obj;
        return "[" + timeStamp + "] " + bundle.getString("message") + "\r\n";
    }

    static private String constructEntry(String msg)
    {
        // record timestamp
        long currentTime = System.currentTimeMillis();
        // convert long time to timestamp format
        SimpleDateFormat sdf = new SimpleDateFormat("MM-d-yyyy H:m:s");
        String timeStamp = sdf.format(currentTime);

        // construct log entry from timestamp + received message
        return "[" + timeStamp + "] " + msg + "\r\n";
    }

    /**
     * Writes entry to log file
     * @param entry entry to add to file
     */
    static private void logEntry(String entry)
    {
        try {
            File file = new File(context.getFilesDir().getAbsolutePath()+"/" + logFileName);
            if (!file.exists())
                file.createNewFile();
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(entry);
            bufferedWriter.close();
            //Toast.makeText(context, "Message successfully logged.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers receiver for CONFIGURATION_CHANGED since it cannot be registered in manifest
     */
    @Override
    public void onCreate()
    {
        receiver = new MyBroadcastReceiver();
        // register receiver for system event
        registerReceiver(receiver, new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        context = getApplicationContext();
        if (intent != null && intent.getFlags() == LOG_MESSAGE)
        {
            // get message to be logged
            String message = intent.getStringExtra(MESSAGE);
            if (message == null)    // don't log empty messages
                return super.onStartCommand(intent, flags, startId);
            // construct entry using message
            String entry = constructEntry(message);
            // log entry to file
            logEntry(entry);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        // unregister receiver
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}

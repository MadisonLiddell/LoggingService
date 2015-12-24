package com.example.LoggingService;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

/**
 * BroadcastReceiver for low battery, power disconnect, configuration change, and boot completed. Starts logging
 * service when boot completed.
 * For all other actions, calls logging service to log the event details and show a notification.
 * @author Madison Liddell
 * @since 11/10/2015
 */
public class MyBroadcastReceiver extends BroadcastReceiver
{
    private static final String lowBatteryMessage = "Low battery";
    private static final String powerDisconnectMessage = "Disconnected from power source";
    private static final String configurationChangeMessage = "Device configuration changed";
    // IDs for notifications
    public static final int lowBatteryID = 0;
    public static final int powerDisconnectID = 1;
    public static final int configurationChangeID = 2;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        switch (intent.getAction())
        {
            case "android.intent.action.BOOT_COMPLETED":                // start logging service
                context.startService(new Intent(context, LoggingService.class));
                break;
            case "android.intent.action.BATTERY_LOW":                   // log to file and notify
                logMessage(context, lowBatteryMessage);
                notifyUser(context, lowBatteryMessage, lowBatteryID);
                Log.i("BroadcastReceiver", " battery low");
                break;
            case "android.intent.action.ACTION_POWER_DISCONNECTED":
                logMessage(context, powerDisconnectMessage);
                notifyUser(context, powerDisconnectMessage, powerDisconnectID);
                Log.i("BroadcastReceiver", " power DC");
                break;
            case "android.intent.action.CONFIGURATION_CHANGED":
                logMessage(context, configurationChangeMessage);
                notifyUser(context, configurationChangeMessage, configurationChangeID);
                Log.i("BroadcastReceiver", " config changed");
                break;
        }
    }

    /**
     * Sends the message by starting service which does the actual logging
     * @param context context to start service from
     * @param message message to be logged
     */
    public void logMessage(Context context, String message) {
        // create intent for logging service
        Intent intent = new Intent(context, LoggingService.class);
        intent.addFlags(LoggingService.LOG_MESSAGE);
        // add the message
        intent.putExtra(LoggingService.MESSAGE, message);
        // send to service
        context.startService(intent);
    }

    /**
     * Build and show notification to user. Plays default notification sound with default vibration
     * @param context context to create from
     * @param title title to display on notification
     * @param id id to use for notification
     */
    private void notifyUser(Context context, String title, int id)
    {
        Log.i("BroadcastReceiver", " trying to notify user");
        Notification notification = new Notification.Builder(context)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_info_black_24dp)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // play sound
                .build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;                              // vibrate device
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notification);
    }
}

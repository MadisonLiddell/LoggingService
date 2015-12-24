package com.example.LoggingService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Simple activity to start the logging service, otherwise it will be started after a boot.
 * @author Madison Liddell
 * @since 10/29/2015
 */
public class LoggingActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, LoggingService.class));
    }
}

package com.mangu.transfer.ui;

import android.util.Log;

/**
 * Created by ok on 8/31/2017.
 */

public class Logger {

    private static final boolean LOGGING = true;
    private static final String TAG = "Countdown Timer: ";

    public static void i(String className, String message) {
        if (LOGGING) {
            Log.i(TAG, className + " - " + message);
        }
    }

    public static void d(String className, String message) {
        if (LOGGING) {
            Log.d(TAG, className + " - " + message);
        }
    }

    public static void v(String className, String message) {
        if (LOGGING) {
            Log.v(TAG, className + " - " + message);
        }
    }

    public static void e(String className, String message, Exception e) {
        if (LOGGING) {
            Log.e(TAG, className + " - " + message, e);
        }
    }

}
package com.sdk.sLog.printer;

import android.util.Log;

import com.sdk.sLog.LogLevel;

public class AndroidPrinter extends Printer {

    @Override
    public boolean logcat(String tag, int level, String message) {
        switch (level) {
            case LogLevel
                    .DEBUG:
                Log.d(tag, message);
                break;
            case LogLevel
                    .VERBOSE:
                Log.v(tag, message);
                break;
            case LogLevel
                    .INFO:
                Log.i(tag, message);
                break;
            case LogLevel
                    .WARN:
                Log.w(tag, message);
                break;
            case LogLevel
                    .ERROR:
                Log.e(tag, message);
                break;
            default:
                return false;
        }
        return true;
    }
}

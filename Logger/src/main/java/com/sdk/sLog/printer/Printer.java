package com.sdk.sLog.printer;

import com.sdk.sLog.Logger;

public abstract class Printer {
    protected Logger _logger;

    public Logger getLogger() {
        return _logger;
    }

    public void attach(Logger logger) {
        _logger = logger;
    }

    public abstract boolean logcat(String tag, int level, String message);
}

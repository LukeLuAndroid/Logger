package com.sdk.sLog.formatter.thread;

import com.sdk.sLog.Logger;

public class DefaultThreadFormatter implements ThreadFormatter {

    @Override
    public void attach(Logger logger) {

    }

    @Override
    public String format(Thread data) {
        return "thread = " + data.getName();
    }
}

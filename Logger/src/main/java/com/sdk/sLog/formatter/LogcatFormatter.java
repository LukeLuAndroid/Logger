package com.sdk.sLog.formatter;

import com.sdk.sLog.LogLevel;
import com.sdk.sLog.Logger;
import com.sdk.sLog.bean.LogInfo;

import java.text.SimpleDateFormat;

public class LogcatFormatter implements Formatter<LogInfo> {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Logger _logger;

    @Override
    public void attach(Logger logger) {
        _logger = logger;
    }

    @Override
    public String format(LogInfo data) {
        StringBuilder builder = new StringBuilder();
        builder.append("tag = " + data.tag());
        builder.append(" [" + LogLevel.getLevelName(data.level()) + "]");
        builder.append(" " + format.format(data.date()));
        if (_logger.getConfiguration().withThread) {
            builder.append(" " + data.thread());
        }
        builder.append(" msg = " + data.msg());

        return builder.toString();

    }
}

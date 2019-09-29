package com.sdk.sLog.printer;

import android.content.Context;

import com.sdk.sLog.Logger;
import com.sdk.sLog.listener.LoggerEncoder;
import com.sdk.sLog.os.MessageLooper;
import com.sdk.sLog.utils.ContextGetter;
import com.sdk.sLog.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class FilePrinter extends Printer {

    private String fileName;
    private LoggerEncoder encoder;
    private static final String DEFAULT_NAME = "fast";
    private static final String DEFAULT_SUFFIX = ".log";
    private MessageLooper _looper;
    private PrintStream _writer;
    private long lastRawTime;
    private static final long DAY_OF_MILLIS = 86400000l;

    public FilePrinter() {
    }

    /**
     * set encoder interface
     *
     * @param encoder
     */
    public void setEncoder(LoggerEncoder encoder) {
        this.encoder = encoder;
    }

    public LoggerEncoder getEncoder() {
        return encoder;
    }

    /**
     * get the log file path
     *
     * @return
     */
    private String getFilePath() {
        String dir = _logger.getConfiguration().baseDir;
        if (dir == null) {
            Context context = ContextGetter.getContext();
            if (context == null) {
                return null;
            }
            dir = context.getFilesDir().getAbsolutePath();
        }

        lastRawTime = IOUtils.getRawTime();

        String name = _logger.getConfiguration().fileName;
        if (name == null || "".equals(name)) {
            fileName = DEFAULT_NAME + "_" + lastRawTime + DEFAULT_SUFFIX;
        }

        File file = new File(dir, "log_" + _logger.getConfiguration().identify + File.separator + fileName);
        return file.getAbsolutePath();
    }

    /**
     * init the file writer
     */
    private synchronized void initWriter() {
        String path = getFilePath();
        if (_writer == null) {
            try {
                File file = new File(path);
                if (!file.exists()) {
                    IOUtils.create(file);
                }
                _writer = new PrintStream(new FileOutputStream(file, true));
                return;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        _writer = System.out;
    }

    @Override
    public void attach(Logger logger) {
        super.attach(logger);
        _looper = new MessageLooper.ThreadMessageLooper(1);
        _looper.loop();
    }

    @Override
    public boolean logcat(String tag, int level, String message) {
        try {
            if (_writer != null && checkNextDay()) {
                _writer.flush();
                _writer.close();
                _writer = null;
            }
            if (_writer == null) {
                initWriter();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (_logger.getConfiguration().encrypt) {
            message = encryptMessage(message);
        }

        final String encryptMessage = message;
        if (_writer != null && _looper != null) {
            _looper.run(() -> _writer.println(encryptMessage));
        }
        return true;
    }

    private String encryptMessage(String message) {
        if (encoder != null) {
            return encoder.encrypt(message);
        }
        return message;
    }

    //是否是新的一天
    private boolean checkNextDay() {
        if (System.currentTimeMillis() - lastRawTime > DAY_OF_MILLIS) {
            return true;
        }
        return false;
    }
}

package com.sdk.sLog;

import com.sdk.sLog.bean.LogInfo;
import com.sdk.sLog.interceptor.Interceptor;
import com.sdk.sLog.interceptor.RealCallInterceptor;
import com.sdk.sLog.interceptor.RealInterceptorChain;
import com.sdk.sLog.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Logger {
    private LogConfiguration logConfiguration;
    private String mTag;
    private List<Interceptor> interceptors = new ArrayList<>();

    /**
     * the tag when log
     *
     * @return
     */
    public String getTag() {
        return mTag;
    }

    public LogConfiguration getConfiguration() {
        return logConfiguration;
    }

    public Logger(Builder builder) {
        this.logConfiguration = builder.config;
        this.mTag = builder.tag;
        interceptors.addAll(logConfiguration.interceptors);
        interceptors.add(new RealCallInterceptor());
        if(logConfiguration.formatter!=null){
            logConfiguration.formatter.attach(this);
        }
    }

    public static class Builder {
        private String tag;
        private LogConfiguration config;

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder setConfig(LogConfiguration config) {
            this.config = config;
            return this;
        }

        public Logger build() {
            if (config == null) {
                throw new RuntimeException("log config can not be null");
            }
            return new Logger(this);
        }
    }

    /**
     * Log a message with level {@link LogLevel#VERBOSE}.
     */
    public void v(String format, Object... args) {
        println(LogLevel.VERBOSE, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#VERBOSE}.
     */
    public void v(String msg) {
        println(LogLevel.VERBOSE, msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#VERBOSE}.
     */
    public void v(String msg, Throwable tr) {
        println(LogLevel.VERBOSE, msg, tr);
    }

    /**
     * Log a message with level {@link LogLevel#DEBUG}.
     *
     * @param format the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    public void d(String format, Object... args) {
        println(LogLevel.DEBUG, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#DEBUG}.
     *
     * @param msg the message to log
     */
    public void d(String msg) {
        println(LogLevel.DEBUG, msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#DEBUG}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public void d(String msg, Throwable tr) {
        println(LogLevel.DEBUG, msg, tr);
    }


    /**
     * Log a message with level {@link LogLevel#INFO}.
     *
     * @param format the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    public void i(String format, Object... args) {
        println(LogLevel.INFO, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#INFO}.
     *
     * @param msg the message to log
     */
    public void i(String msg) {
        println(LogLevel.INFO, msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#INFO}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public void i(String msg, Throwable tr) {
        println(LogLevel.INFO, msg, tr);
    }

    /**
     * Log a message with level {@link LogLevel#WARN}.
     *
     * @param format the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    public void w(String format, Object... args) {
        println(LogLevel.WARN, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#WARN}.
     *
     * @param msg the message to log
     */
    public void w(String msg) {
        println(LogLevel.WARN, msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#WARN}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public void w(String msg, Throwable tr) {
        println(LogLevel.WARN, msg, tr);
    }

    /**
     * Log a message with level {@link LogLevel#ERROR}.
     *
     * @param format the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    public void e(String format, Object... args) {
        println(LogLevel.ERROR, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#ERROR}.
     *
     * @param msg the message to log
     */
    public void e(String msg) {
        println(LogLevel.ERROR, msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#ERROR}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public void e(String msg, Throwable tr) {
        println(LogLevel.ERROR, msg, tr);
    }

    private void println(int logLevel, String msg, Throwable tr) {
        if ((logLevel & logConfiguration.logLevel) == 0) {
            return;
        }
        printlnInternal(logLevel, (msg == null || msg.length() == 0) ? Utils.getThrowabeString(tr)
                : msg + Utils.getLineSeparator() + Utils.getThrowabeString(tr));
    }

    private void println(int logLevel, String format, Object... args) {
        if ((logLevel & logConfiguration.logLevel) == 0) {
            return;
        }
        printlnInternal(logLevel, formatArgs(format, args));
    }

    private void printlnInternal(int logLevel, String msg) {
        if (!sLog.instance().isStaticOpen()) {
            return;
        }

        String tag = mTag;
        String thread = logConfiguration.withThread
                ? logConfiguration.threadFormatter.format(Thread.currentThread())
                : null;

        LogInfo log = new LogInfo(tag, msg, logLevel, thread, new Date());

        if (interceptors != null) {
            RealInterceptorChain chain = new RealInterceptorChain(logConfiguration.interceptors, log, 0);
            log = chain.proceed(log);
        }

        //log has been intercepted
        if (log == null) {
            return;
        }

        String logFormat = "";
        //format the log info to message
        if (logConfiguration.formatter != null) {
            logFormat = logConfiguration.formatter.format(log);
        }

        // TODO: 2019/9/11 预处理

        //printer the log to local
        if (logConfiguration.printer != null && log != null) {
            logConfiguration.printer.logcat(tag, logLevel, logFormat);
        }
    }

    /**
     * Format a string with arguments.
     *
     * @param format the format string, null if just to concat the arguments
     * @param args   the arguments
     * @return the formatted string
     */
    private String formatArgs(String format, Object... args) {
        if (format != null) {
            return String.format(format, args);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, N = args.length; i < N; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(args[i]);
            }
            return sb.toString();
        }
    }


}

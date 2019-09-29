package com.sdk.sLog;

import com.sdk.sLog.bean.LogInfo;
import com.sdk.sLog.formatter.Formatter;
import com.sdk.sLog.formatter.LogcatFormatter;
import com.sdk.sLog.formatter.thread.DefaultThreadFormatter;
import com.sdk.sLog.formatter.thread.ThreadFormatter;
import com.sdk.sLog.interceptor.Interceptor;
import com.sdk.sLog.listener.LoggerEncoder;
import com.sdk.sLog.printer.AndroidPrinter;
import com.sdk.sLog.printer.FilePrinter;
import com.sdk.sLog.printer.Printer;

import java.util.ArrayList;
import java.util.List;

public class LogConfiguration {
    public final int logLevel;
    public final List<Interceptor> interceptors;
    public final ThreadFormatter threadFormatter;
    public final Printer printer;
    public final Formatter<LogInfo> formatter;
    /**
     * Whether we should log with thread info.
     */
    public final boolean withThread;
    /**
     * the baseDir when use file printer
     */
    public final String baseDir;
    /**
     * the only identify info to the Logger
     */
    public final String identify;

    /**
     * if printer to file ,the we can set a fileName
     */
    public final String fileName;

    /**
     * is support encrypt
     */
    public final boolean encrypt;

    private LogConfiguration(Builder builder) {
        logLevel = builder.level;
        threadFormatter = builder.threadFormatter;
        withThread = builder.withThread;
        printer = builder.printer;
        formatter = builder.formatter;
        baseDir = builder.logDir;
        interceptors = builder.interceptors;
        identify = builder.identify;
        encrypt = builder.encrypt;
        fileName = builder.fileName;
    }

    public static class Builder {

        private static final int DEFAULT_LOG_LEVEL = LogLevel.ALL;

        private static final String DEFAULT_TAG = "S-LOG";
        /**
         * The log level, the logs would not be printed.
         */
        private int level = DEFAULT_LOG_LEVEL;
        //the only identify
        private String identify = DEFAULT_TAG;
        private List<Interceptor> interceptors;
        private ThreadFormatter threadFormatter;

        /**
         * Whether we should log with thread info.
         */
        private boolean withThread = true;
        private Printer printer;
        private Formatter<LogInfo> formatter;
        private String logDir;
        private String fileName;
        private boolean encrypt;
        private LoggerEncoder encoder;

        public Builder() {

        }

        public Builder(LogConfiguration configuration) {
            if (configuration == null) {
                throw new RuntimeException("you have to init logConfiguration before use LoggerFactory get Logger");
            }

            level = configuration.logLevel;
            threadFormatter = configuration.threadFormatter;
            withThread = configuration.withThread;
            printer = configuration.printer;
            formatter = configuration.formatter;
            logDir = configuration.baseDir;
            identify = configuration.identify;
            if (configuration.interceptors != null) {
                interceptors = new ArrayList<>(configuration.interceptors);
            }
        }

        public Builder logLevel(int level) {
            this.level = level;
            return this;
        }

        /**
         * Add an interceptor.
         *
         * @param interceptor the interceptor to add
         * @return the builder
         * @since 1.3.0
         */
        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptors == null) {
                interceptors = new ArrayList<>();
            }
            interceptors.add(interceptor);
            return this;
        }

        /**
         * Set the thread formatter used when logging.
         *
         * @param threadFormatter the thread formatter used when logging
         * @return the builder
         */
        public Builder threadFormatter(ThreadFormatter threadFormatter) {
            this.threadFormatter = threadFormatter;
            return this;
        }

        public Builder withThread(boolean thread) {
            this.withThread = thread;
            return this;
        }

        public Builder printer(Printer printer) {
            this.printer = printer;
            return this;
        }

        public Builder formatter(Formatter formatter) {
            this.formatter = formatter;
            return this;
        }

        /**
         * 文件
         *
         * @return
         */
        public Builder ofFile() {
            if (printer == null || !(printer instanceof FilePrinter)) {
                this.printer = new FilePrinter();
            }
            return this;
        }

        public Builder dir(String dir) {
            this.logDir = dir;
            return this;
        }

        public Builder fileName(String name) {
            this.fileName = name;
            return this;
        }

        public Builder encryptEnable(boolean encrypt) {
            this.encrypt = encrypt;
            return this;
        }

        public Builder identify(String tag) {
            this.identify = tag;
            return this;
        }

        public Builder encoder(LoggerEncoder encoder) {
            this.encoder = encoder;
            return this;
        }

        public LogConfiguration build() {
            checkFields();
            return new LogConfiguration(this);
        }

        private void checkFields() {
            if (identify == null || "".equals(identify)) {
                throw new RuntimeException("identify can not be null or empty");
            }

            if (threadFormatter == null) {
                threadFormatter = new DefaultThreadFormatter();
            }

            if (formatter == null) {
                formatter = new LogcatFormatter();
            }

            if (printer == null) {
                printer = new AndroidPrinter();
            }

            //if is the filePrinter,then set the encoder to it.
            if (encoder != null && printer instanceof FilePrinter) {
                ((FilePrinter) printer).setEncoder(encoder);
            }

            if (interceptors == null) {
                interceptors = new ArrayList<>();
            }
        }
    }

    /**
     * choose the printer
     *
     * @param printer
     * @return
     */
    public static LogConfiguration ofPrinter(Printer printer) {
        if (printer == null) {
            return sLog.instance().getConfig();
        }
        Builder builder = new Builder(sLog.instance().getConfig());
        builder.printer(printer);
        return builder.build();
    }

    /**
     * use file printer
     *
     * @return
     */
    public static LogConfiguration ofFile() {
        Builder builder = new Builder(sLog.instance().getConfig());
        builder.printer(new FilePrinter());
        return builder.build();
    }

    /**
     * the log output format
     *
     * @param formatter
     * @return
     */
    public static LogConfiguration ofFormatter(Formatter formatter) {
        if (formatter == null) {
            return sLog.instance().getConfig();
        }
        Builder builder = new Builder(sLog.instance().getConfig());
        builder.formatter(formatter);
        return builder.build();
    }

    /**
     * account the param config to build the config
     *
     * @param configuration
     * @return
     */
    public static LogConfiguration.Builder of(LogConfiguration configuration) {
        Builder builder = new Builder(configuration);
        return builder;
    }

    /**
     * create a builder，then we can build other params
     *
     * @return
     */
    public static LogConfiguration.Builder of() {
        Builder builder = new Builder(sLog.instance().getConfig());
        return builder;
    }

    public static LogConfiguration ofIdentify(String identify) {
        Builder builder = new Builder();
        builder.identify(identify);
        return builder.build();
    }

}

package com.sdk.sLog;

public class LogLevel {

    /**
     * Log level for XLog.v.
     */
    public static final int VERBOSE = 0x00001;

    /**
     * Log level for XLog.d.
     */
    public static final int DEBUG = 0x00010;

    /**
     * Log level for XLog.i.
     */
    public static final int INFO = 0x00100;

    /**
     * Log level for XLog.w.
     */
    public static final int WARN = 0x01000;

    /**
     * Log level for XLog.e.
     */
    public static final int ERROR = 0x10000;

    /**
     * Log level for XLog#init, printing all logs.
     */
    public static final int ALL = 0x11111;

    /**
     * Log level for XLog#init, printing no log.
     */
    public static final int NONE = 0x00000;

    /**
     * Get a name representing the specified log level.
     * <p>
     * The returned name may be<br>
     * {@link LogLevel#VERBOSE}: "VERBOSE"<br>
     * {@link LogLevel#DEBUG}: "DEBUG"<br>
     * {@link LogLevel#INFO}: "INFO"<br>
     * {@link LogLevel#WARN}: "WARN"<br>
     * {@link LogLevel#ERROR}: "ERROR"<br>
     * {default #FAULT}
     *
     * @param logLevel the log level to get name for
     * @return the name
     */
    public static String getLevelName(int logLevel) {
        String levelName;
        switch (logLevel) {
            case VERBOSE:
                levelName = "VERBOSE";
                break;
            case DEBUG:
                levelName = "DEBUG";
                break;
            case INFO:
                levelName = "INFO";
                break;
            case WARN:
                levelName = "WARN";
                break;
            case ERROR:
                levelName = "ERROR";
                break;
            default:
                levelName = "fault";
                break;
        }
        return levelName;
    }

    /**
     * Get a short name representing the specified log level.
     * <p>
     * The returned name may be<br>
     * {@link LogLevel#VERBOSE}<br>
     * {@link LogLevel#VERBOSE}: "V"<br>
     * {@link LogLevel#DEBUG}: "D"<br>
     * {@link LogLevel#INFO}: "I"<br>
     * {@link LogLevel#WARN}: "W"<br>
     * {@link LogLevel#ERROR}: "E"<br>
     *  default #FAULT
     *
     * @param logLevel the log level to get short name for
     * @return the short name
     */
    public static String getShortLevelName(int logLevel) {
        String levelName;
        switch (logLevel) {
            case VERBOSE:
                levelName = "V";
                break;
            case DEBUG:
                levelName = "D";
                break;
            case INFO:
                levelName = "I";
                break;
            case WARN:
                levelName = "W";
                break;
            case ERROR:
                levelName = "E";
                break;
            default:
                levelName = "F";
                break;
        }
        return levelName;
    }
}
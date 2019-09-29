package com.sdk.sLog.bean;

import java.util.Date;

public class LogInfo {
    private String _msg;
    private int _level;
    private Date _date;
    private String _thread;
    private String _tag;

    public LogInfo(String tag, String msg, int level, String thread, Date _date) {
        _tag = tag;
        this._msg = msg;
        this._level = level;
        this._date = _date;
        this._thread = thread;
    }

    public final Date date() {
        return _date;
    }

    public final void date(Date _date) {
        this._date = _date;
    }

    public final int level() {
        return _level;
    }

    public final void level(int _level) {
        this._level = _level;
    }

    public final String msg() {
        return _msg;
    }

    public final void msg(String _msg) {
        this._msg = _msg;
    }

    public final String thread() {
        return _thread;
    }

    public final void thread(String _threadName) {
        this._thread = _threadName;
    }

    public final String tag() {
        return _tag;
    }

    public final void tag(String tag) {
        this._tag = tag;
    }
}

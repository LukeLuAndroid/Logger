package com.sdk.sLog.formatter;


import com.sdk.sLog.Logger;

public interface Formatter<T> {
    void attach(Logger logger);

    String format(T data);
}

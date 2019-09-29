package com.sdk.sLog.interceptor;

import com.sdk.sLog.bean.LogInfo;

public interface Interceptor {
    LogInfo intercept(Chain chain);

    interface Chain {
        LogInfo proceed(LogInfo info);
    }

}

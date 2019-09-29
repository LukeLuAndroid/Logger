package com.sdk.sLog.interceptor;

import com.sdk.sLog.bean.LogInfo;

public class RealCallInterceptor implements Interceptor {

    @Override
    public LogInfo intercept(Chain chain) {
        RealInterceptorChain interceptorChain = (RealInterceptorChain) chain;
        return interceptorChain.getLogInfo();
    }
}

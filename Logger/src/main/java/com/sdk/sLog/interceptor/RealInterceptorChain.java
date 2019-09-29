package com.sdk.sLog.interceptor;

import com.sdk.sLog.bean.LogInfo;

import java.util.List;

public class RealInterceptorChain implements Interceptor.Chain {
    private final List<Interceptor> interceptors;
    private final LogInfo logInfo;
    private final int index;
    private int calls;

    public RealInterceptorChain(List<Interceptor> interceptors, LogInfo info, int index) {
        this.interceptors = interceptors;
        this.logInfo = info;
        this.index = index;
    }

    public LogInfo getLogInfo() {
        return logInfo;
    }

    @Override
    public LogInfo proceed(LogInfo info) {

        if (index >= interceptors.size()) throw new AssertionError();

        calls++;

        RealInterceptorChain next = new RealInterceptorChain(interceptors, info, index + 1);
        Interceptor interceptor = interceptors.get(index);
        LogInfo response = interceptor.intercept(next);

        if (index + 1 < interceptors.size() && next.calls != 1) {
            throw new IllegalStateException("interceptor " + interceptor
                    + " must call proceed() exactly once");
        }

        return response;
    }
}

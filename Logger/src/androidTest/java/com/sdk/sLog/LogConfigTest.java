package com.sdk.sLog;

import android.support.test.runner.AndroidJUnit4;

import com.sdk.sLog.bean.LogInfo;
import com.sdk.sLog.formatter.LogcatFormatter;
import com.sdk.sLog.formatter.thread.DefaultThreadFormatter;
import com.sdk.sLog.interceptor.Interceptor;
import com.sdk.sLog.interceptor.RealInterceptorChain;
import com.sdk.sLog.printer.AndroidPrinter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LogConfigTest {

    @Test
    public void LogConfigTest() {
        LogConfiguration.Builder builder = new LogConfiguration.Builder();
        builder.identify("com.sdk.sLog.test");
        LogConfiguration configuration = builder.build();

        Assert.assertTrue(configuration.identify.equals("com.sdk.sLog.test"));
        Assert.assertTrue(!configuration.encrypt);
        Assert.assertTrue(configuration.withThread);
        Assert.assertTrue(configuration.formatter instanceof LogcatFormatter);
        Assert.assertEquals(configuration.baseDir, null);
        Assert.assertTrue(configuration.printer instanceof AndroidPrinter);
        Assert.assertTrue(configuration.threadFormatter instanceof DefaultThreadFormatter);
        Assert.assertEquals(configuration.logLevel, LogLevel.ALL);
        Assert.assertTrue(configuration.interceptors.size() == 0);

        LogConfiguration configuration1 = LogConfiguration.of(configuration).addInterceptor((chain) -> {
            RealInterceptorChain interceptorChain = (RealInterceptorChain) chain;
            return interceptorChain.getLogInfo();
        }).logLevel(LogLevel.DEBUG | LogLevel.INFO).build();

        Assert.assertTrue(configuration1.interceptors.size() == 1);
        Assert.assertEquals(configuration1.logLevel, LogLevel.DEBUG | LogLevel.INFO);
    }
}

package com.sdk.sLog;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.sdk.sLog.Logger;
import com.sdk.sLog.LoggerFactory;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LoggerTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Logger logger = LoggerFactory.getLogger("LoggerTest");
        logger.d("hahaha test");
    }
}

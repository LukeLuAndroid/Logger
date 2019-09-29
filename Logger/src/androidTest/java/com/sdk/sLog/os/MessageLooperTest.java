package com.sdk.sLog.os;

import android.os.Handler;
import android.os.Looper;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RunWith(AndroidJUnit4.class)
public class MessageLooperTest {
    volatile int index = 0;
    Lock lock = new ReentrantLock();

    @Test
    public void testMessage() {
        int size = 20;

        MessageLooper looper = new MessageLooper.ThreadMessageLooper(2);
        looper.loop();

        for (int i = 0; i < size; i++) {
            looper.run(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock.lock();
                        index++;
                        Log.e("MessageLooperTest", "current.index=" + index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Assert.assertEquals(size, index);
        }, 2000);

//        Assert.assertEquals(size, index);
    }
}

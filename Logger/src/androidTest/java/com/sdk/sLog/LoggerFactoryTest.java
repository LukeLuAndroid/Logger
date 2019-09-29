package com.sdk.sLog;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.sdk.sLog.sLog;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LoggerFactoryTest {

    @Test
    public void testCheckOutDate() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        File file = new File(appContext.getFilesDir(), "log");
        if (!file.exists()) {
            file.mkdirs();
        }

        int size = file.list().length;

        try {
            File testFile = new File(file, System.currentTimeMillis() + File.separator + "fast.log");
            if (!testFile.exists()) {
                testFile.getParentFile().mkdirs();
                testFile.createNewFile();
                testFile.getParentFile().setLastModified(System.currentTimeMillis() - 8 * 86400000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(size + 1, file.list().length);

        sLog.instance().checkOutDateLog(file.getAbsolutePath());

        assertEquals(size, file.list().length);
    }
}

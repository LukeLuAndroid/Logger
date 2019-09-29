package com.sdk.sLog.upload;

import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.sdk.sLog.LogConfiguration;
import com.sdk.sLog.os.ThreadPool;
import com.sdk.sLog.sLog;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;

@RunWith(AndroidJUnit4.class)
public class LogUploadManagerTest {

    @Test
    public void testLogUpload() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        LogConfiguration configuration = LogConfiguration.ofIdentify("com.sdk.sLog.test");
        String baseDir = appContext.getFilesDir().getAbsolutePath();

        File fileDir = new File(baseDir, "log_" + configuration.identify);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        for (File f : fileDir.listFiles()) {
            f.delete();
        }

        for (int i = 0; i < 6; i++) {
            try {
                File testFile = new File(fileDir, "fast_" + System.currentTimeMillis() + ".log");
                if (!testFile.exists()) {
                    testFile.getParentFile().mkdirs();
                    testFile.createNewFile();
                }
                Thread.sleep(5);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Assert.assertEquals(fileDir.list().length, 6);

        sLog.instance().setUploader(new Uploader() {
            @Override
            public void upload(File[] list, UploadResultListener listener) {
                for (File f : list) {
                    listener.onSuccess(f);
                }
            }
        }).init(appContext, configuration);

        new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
            Assert.assertEquals(fileDir.list().length, 0);
        }, 2000);
    }
}

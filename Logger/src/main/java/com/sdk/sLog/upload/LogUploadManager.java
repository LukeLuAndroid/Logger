package com.sdk.sLog.upload;

import com.sdk.sLog.os.ThreadPool;
import com.sdk.sLog.sLog;

import java.io.File;

public class LogUploadManager {

    private static class Singleton {
        static LogUploadManager instance = new LogUploadManager();
    }

    public static LogUploadManager instance() {
        return Singleton.instance;
    }

    private Uploader getUploader() {
        return sLog.instance().getUploader();
    }

    public void checkUpLoader(String dir) {
        File dirFile = new File(dir);
        File[] list = dirFile.listFiles();
        if (list != null && list.length != 0) {
            ThreadPool.submitUiThread(() -> {
                if (getUploader() != null) {
                    getUploader().upload(list, new DefaultUploadImpl());
                }
            });
        }
    }
}

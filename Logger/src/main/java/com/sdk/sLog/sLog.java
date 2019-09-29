package com.sdk.sLog;

import android.content.Context;
import android.util.Log;

import com.sdk.sLog.os.ThreadPool;
import com.sdk.sLog.upload.LogUploadManager;
import com.sdk.sLog.upload.Uploader;
import com.sdk.sLog.utils.IOUtils;
import com.sdk.sLog.utils.Utils;

import java.io.File;

public class sLog {
    private static sLog _instance;
    private LogConfiguration config;
    private boolean mIsInited;
    private Uploader uploader;
    private Context mContext;

    public static sLog instance() {
        if (_instance == null) {
            synchronized (sLog.class) {
                if (_instance == null) {
                    _instance = new sLog();
                }
            }
        }
        return _instance;
    }

    public void init(Context ctx, LogConfiguration c) {
        if (!mIsInited) {
            mContext = ctx;
            this.config = c;
            if (ctx != null && c != null) {
                checkOutDateLog(ctx, c);
            } else {
                throw new RuntimeException("context or LogConfiguration can not be null");
            }
            mIsInited = true;
        }
    }

    public LogConfiguration getConfig() {
        return config;
    }

    private boolean _isStaticOpen = true;

    public void openStaticLog() {
        _isStaticOpen = true;
    }

    public void closeStaticLog() {
        _isStaticOpen = false;
    }

    public boolean isStaticOpen() {
        return _isStaticOpen;
    }

    /**
     * 检查过期的日志文件
     */
    private static void checkOutDateLog(Context ctx, LogConfiguration config) {
        String baseDir = ctx.getFilesDir().getAbsolutePath();
        if (config != null && config.baseDir != null) {
            baseDir = config.baseDir;
        }
        final String logDir = new File(baseDir, "log_" + config.identify).getAbsolutePath();
        ThreadPool.submitSingleTask(() -> {
            checkOutDateLog(logDir);
            LogUploadManager.instance().checkUpLoader(logDir);
        });
    }

    /**
     * check date out log
     * the cache interval is 7days
     *
     * @param baseDir
     */
    public static void checkOutDateLog(String baseDir) {
        long miniRawTime = IOUtils.getRawTime() - 7 * 24 * 3600 * 1000L;

        File dir = new File(baseDir);
        if (dir.exists() && dir.isDirectory()) {
            try {
                File[] fileList = dir.listFiles();
                if (fileList != null && fileList.length != 0) {
                    int deleteIndex = 0;
                    for (int i = 0; i < fileList.length; i++) {
                        File file = fileList[i];
                        String fileName = file.getName();

                        try {
                            if (file.lastModified() >= miniRawTime) {
                                continue;
                            }
                        } catch (Throwable th) {
                            Log.d("LoggerManager", "log file that has invalid format: " + fileName);
                        }

                        if (Utils.deleteFile(file)) {
                            ++deleteIndex;
                        }
                    }
                    Log.d("LoggerManager", "Number of overdue log files that has deleted: " + deleteIndex);
                    return;
                }
                return;
            } catch (Throwable th) {
                Log.e("LoggerManager", th != null ? th.toString() : "");
            }
        }
    }

    public Uploader getUploader() {
        return uploader;
    }

    public sLog setUploader(Uploader uploader) {
        this.uploader = uploader;
        return this;
    }
}

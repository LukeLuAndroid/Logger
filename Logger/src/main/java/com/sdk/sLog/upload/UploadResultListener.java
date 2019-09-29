package com.sdk.sLog.upload;

import java.io.File;

public interface UploadResultListener {
    /**
     * upload success from server
     */
    void onSuccess(File[] list);
    /**
     * upload success from server
     */
    void onSuccess(File file);

    /**
     * upload fail from server
     */
    void onFail(File file);
}

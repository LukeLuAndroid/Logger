package com.sdk.sLog.upload;

import java.io.File;

public interface Uploader {
    /**
     * 文件上传成功请务必删除
     *
     * @param list
     */
    void upload(File[] list, UploadResultListener listener);
}

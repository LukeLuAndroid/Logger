package com.sdk.sLog.upload;

import com.sdk.sLog.utils.Utils;

import java.io.File;

public class DefaultUploadImpl implements UploadResultListener {

    @Override
    public void onSuccess(File[] list) {
        if (list != null) {
            for (File f : list) {
                onSuccess(f);
            }
        }
    }

    @Override
    public void onSuccess(File file) {
        Utils.deleteFile(file);
        File parent = file.getParentFile();
        try {
            if (parent != null && parent.list().length == 0) {
                parent.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFail(File file) {

    }
}

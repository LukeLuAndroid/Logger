package com.sdk.sLog.utils;

import android.os.Build;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {

    public static String getThrowabeString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        StringWriter writer = new StringWriter();
        tr.printStackTrace(new PrintWriter(writer));

        return writer.toString();
    }

    public static String getLineSeparator() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return "\n";
        }
        return System.lineSeparator();
    }

    /**
     * delete all file under the file if the file is directory
     * other will delete the file
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] list = file.listFiles();
                for (File f : list) {
                    if (f.isDirectory()) {
                        deleteFile(f);
                    } else {
                        f.delete();
                    }
                }
            }
            file.delete();
        }
        return !file.exists();
    }
}

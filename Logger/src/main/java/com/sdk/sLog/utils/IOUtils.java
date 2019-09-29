package com.sdk.sLog.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class IOUtils {
    static final int IOBuffLength = 1024 * 4;

    //region Interface
    public interface FileInputAccess {
        void onOpened(FileInputStream inputStream);
    }

    //endregion

    //region Create File
    public static File create(String path) throws IOException {
        return create(new File(path));
    }

    public static File create(File file) throws IOException {
        if (!file.exists()) {
            File parent = file.getParentFile();

            boolean mkdirs = false;
            boolean createFile;

            if (parent != null && !parent.exists())
                mkdirs = parent.mkdirs();

            createFile = file.createNewFile();

            if (!createFile)
                throw new IOException("newInstance file " + file + " failed because mkdirs:" + mkdirs);
        }

        return file;
    }
    //endregion


    //region Write
    public static void write(InputStream in, OutputStream out) throws IOException {
        byte[] buff = new byte[IOBuffLength];
        int buffLen;

        while ((buffLen = in.read(buff, 0, buff.length)) != -1) {
            out.write(buff, 0, buffLen);
        }
    }

    /*
     * 移动文件src到tar
     */
    public static void writeTo(File src, File tar) throws IOException {
        if (src.getPath().equals(tar.getPath()))
            return;

        if (!src.exists()) {
            throw new IOException("file:" + src.getPath() + "not exists");
        }

        if (!src.canRead()) {
            throw new IOException("file:" + src.getPath() + "cannot read");
        }

        FileInputStream srcStream = null;
        FileOutputStream tarStream = null;

        try {
            create(tar);

            if (!tar.canWrite()) {
                throw new IOException("file:" + tar.getPath() + "cannot write");
            }

            srcStream = new FileInputStream(src);
            tarStream = new FileOutputStream(tar);

            write(srcStream, tarStream);

        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            if (srcStream != null)
                srcStream.close();

            if (tarStream != null)
                tarStream.close();
        }
    }

    //endregion
    public static void input(File file, FileInputAccess access) throws IOException {
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
            access.onOpened(inputStream);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    //region File
    public static void copy(File src, File tar) throws IOException {
        if (src.getPath().equals(tar.getPath()))
            return;

        if (src.isDirectory()) {
            if (!tar.exists()) {
                tar.mkdirs();
            }

            if (!tar.isDirectory())
                throw new IOException("tar path not is Directory");

            File[] files = src.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        copyFile(file, tar);
                    } else {
                        copy(file, new File(tar, file.getName()));
                    }
                }
            } else {
                copyFile(src, tar);
            }
        }
    }

    public static void copyFile(File src, File tar) throws IOException {
        if (tar.exists()) {
            if (tar.isDirectory())
                IOUtils.writeTo(src, new File(tar, src.getName()));
            else IOUtils.writeTo(src, tar);
        } else {
            String path = tar.getPath();
            if (File.separator.equals(path.charAt(path.length() - 1) + ""))
                IOUtils.writeTo(src, new File(tar, src.getName()));
            else IOUtils.writeTo(src, tar);
        }
    }

    public static boolean delete(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            if (children != null)
                for (int i = 0; i < children.length; i++) {
                    boolean success = delete(new File(file, children[i]));
                    if (!success) {
                        return false;
                    }
                }
        }

        return file.delete();
    }

    public static List<File> list(File file, FileFilter fileFilter) {
        List<File> list = new ArrayList<File>();
        listFiles(file, list, fileFilter);
        return list;
    }

    public static List<File> list(File file) {
        return list(file, file1 -> true);
    }

    private static void listFiles(@NonNull File file, List<File> files, FileFilter
            fileFilter) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles(fileFilter);
            if (fileList != null)
                for (File f : fileList)
                    listFiles(f, files, fileFilter);
        } else
            files.add(file);
    }

    public interface FileVisitor {
        void visit(String parent, File file);
    }

    public static void foreach(File file, FileVisitor visitor) {
        foreach(file, null, visitor);
    }

    private static void foreach(File file, String parent, FileVisitor visitor) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();

            if (files != null) {
                if (parent == null)
                    parent = "";
                else
                    parent = parent + File.separator + file.getName();

                for (File f : files) {
                    foreach(f, parent, visitor);
                }
            }
        } else {
            if (parent == null) {
                parent = "";
            }

            visitor.visit(parent, file);
        }
    }
    //endregion

    public static long getRawTime() {
        try {
            return (System.currentTimeMillis() + (long) TimeZone.getDefault().getRawOffset()) / 86400000L * 86400000L - (long) TimeZone.getDefault().getRawOffset();
        } catch (Throwable var3) {
            var3.printStackTrace();
            return -1L;
        }
    }
}

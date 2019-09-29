package com.sdk.sLog.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class DigestUtils
{
    public static String md5FromFile(@NonNull String filepath) throws NoSuchAlgorithmException, FileNotFoundException
    {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        File f = new File(filepath);
        String output = "";
        InputStream is = new FileInputStream(f);
        byte[] buffer = new byte[8192];
        int read = 0;
        try
        {
            while ((read = is.read(buffer)) > 0)
            {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            output = bigInt.toString(16);
            for (; output.length() < 32; )
            {
                output = "0" + output;
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to process file for MD5", e);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
            }
        }
        return output;
    }

    public static String md5(@NonNull String val)
    {
        return md5(val.getBytes());
    }

    public static String md5(@NonNull byte[] val)
    {
        byte[] hash;

        try
        {
            hash = MessageDigest.getInstance("MD5").digest(val);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash)
        {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    public static String sha1(@NonNull byte[] val)
    {
        byte[] hash;

        try
        {
            hash = MessageDigest.getInstance("SHA").digest(val);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash)
        {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    public static String sha1(@NonNull String val)
    {
        return sha1(val.getBytes());
    }
}

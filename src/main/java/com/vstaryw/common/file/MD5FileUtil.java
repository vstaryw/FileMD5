package com.vstaryw.common.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5FileUtil {

    private static Logger logger = LoggerFactory.getLogger(MD5FileUtil.class.getName());

    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    protected static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5FileUtil messagedigest初始化失败", e);
        }
    }

    public static String getFileMD5String(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        messagedigest.update(byteBuffer);
        return bufferToHex(messagedigest.digest());
    }

    public static String getBigFileMD5String(File file) throws IOException {

        FileInputStream in = new FileInputStream(file);
        byte[] byteStart = new byte[500 * 1024];
        in.read(byteStart, 0, 500 * 1024);
        long index = file.length() / 6;
        byte[] byte1 = new byte[200 * 1024];
        in.skip(index * 1);
        in.read(byte1);
        byte[] byte2 = new byte[200 * 1024];
        in.skip(index * 2);
        in.read(byte2);
        byte[] byte3 = new byte[200 * 1024];
        in.skip(index * 3);
        in.read(byte3);
        byte[] byte4 = new byte[200 * 1024];
        in.skip(index * 4);
        in.read(byte4);
        byte[] byte5 = new byte[200 * 1024];
        in.skip(index * 5);
        in.read(byte5);
        byte[] byteEnd = new byte[500 * 1024];
        in.skip(file.length() - 500 * 1024);
        in.read(byteEnd);
        byte[] bytes = new byte[byteStart.length + byte1.length + byte2.length + byte3.length + byte4.length + byte5.length + byteEnd.length];
        System.arraycopy(byteStart, 0, bytes, 0, byteStart.length);
        System.arraycopy(byte1, 0, bytes, byteStart.length, byte1.length);
        System.arraycopy(byte2, 0, bytes, byteStart.length + byte1.length, byte2.length);
        System.arraycopy(byte3, 0, bytes, byteStart.length + byte1.length + byte2.length, byte3.length);
        System.arraycopy(byte4, 0, bytes, byteStart.length + byte1.length + byte2.length + byte3.length, byte4.length);
        System.arraycopy(byte5, 0, bytes, byteStart.length + byte1.length + byte2.length + byte3.length + byte4.length, byte5.length);
        System.arraycopy(byteEnd, 0, bytes, byteStart.length + byte1.length + byte2.length + byte3.length + byte4.length + byte5.length, byteEnd.length);
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}

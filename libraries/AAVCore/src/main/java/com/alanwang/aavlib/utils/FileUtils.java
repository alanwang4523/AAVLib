package com.alanwang.aavlib.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: AlanWang4523.
 * Date: 19/4/16 00:04.
 * Mail: alanwang4523@gmail.com
 */
public class FileUtils {

    /**
     * 从输入流中获取字节数据
     * @param is
     * @return
     */
    public static byte[] getBytes(InputStream is) {
        byte[] data = null;
        try {
            data = new byte[is.available()];
            is.read(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * 删除文件以及文件夹
     * @param file
     */
    public static boolean deleteFile(File file) {
        boolean result = false;
        if (file.isFile()) {
            result = file.delete();
            return result;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                result = file.delete();
                return result;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            result = file.delete();
        }
        return result;
    }
}

package com.jerry.wechatservice.util;

import java.io.Closeable;
import java.io.File;

import com.jerry.wechatservice.MyApplication;

/**
 * 文件处理类
 *
 * @author Tina
 */
public class FileUtil {

    public static File getSaveFile() {
        return new File(MyApplication.getInstance().getFilesDir(), "pic.jpg");
    }

    /**
     * 清空文件：参数为文件夹时，只清理其内部文件，不清理本身, 参数为文件时，删除
     */
    public static void clearFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File f : files) {
                if (f.isDirectory()) {
                    clearFile(f);
                } else {
                    f.delete();
                }
            }
        } else {
            file.delete();
        }
    }

    public static void close(final Closeable... closeables) {
        try {
            for (Closeable closeable : closeables) {
                if (closeable == null) {
                    continue;
                }
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

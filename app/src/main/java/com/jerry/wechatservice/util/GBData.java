package com.jerry.wechatservice.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;

public class GBData {

    private static final String TAG = "GBData";
    public static ImageReader reader;

    /**
     * @return
     */
    public static boolean getPic(int left, int top, int width, int height) {
        if (reader == null || width <= 0 || height <= 0) {
            LogUtils.w("getColor: reader is null");
            return false;
        }

        Image image = reader.acquireLatestImage();

        if (image == null) {
            LogUtils.w("getColor: image is null");
            return false;
        }
        int iwidth = image.getWidth();
        int iheight = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * iwidth;
        Bitmap bitmap = Bitmap.createBitmap(iwidth + rowPadding / pixelStride, iheight, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, left, top, width, height);
        image.close();
        LogUtils.i("image data captured");

        //保存截屏结果，如果要裁剪图片，在这里处理bitmap
        if (bitmap != null) {
            try {
                File fileImage = new File(FileUtil.getSaveFile().getAbsolutePath());
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                    LogUtils.i("image file created");
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                LogUtils.i("screen image saved");
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
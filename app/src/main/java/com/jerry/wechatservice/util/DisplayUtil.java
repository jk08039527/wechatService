package com.jerry.wechatservice.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.DimenRes;

import com.jerry.wechatservice.MyApplication;

import java.lang.reflect.Method;

public class DisplayUtil {

    private static final int NOTCH_IN_SCREEN_VIVO = 0x00000020;//是否有凹槽

    private DisplayUtil() {}

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(double pxValue) {
        return (int) (pxValue / getDisplayDensity() + 0.5f);
    }

    /**
     * 获取屏幕密度
     */
    public static float getDisplayDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * 获取手机屏幕的像素高
     */
    public static int getDisplayHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取手机屏幕的像素宽
     */
    public static int getDisplayWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(double dipValue) {
        return (int) (dipValue * getDisplayDensity() + 0.5f);
    }

    /**
     * 获取dimen的像素值
     */
    public static int getDimensionPixelSize(@DimenRes int dimenId) {
        return MyApplication.getInstance().getResources().getDimensionPixelSize(dimenId);
    }

    /**
     * 获取手机状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        if (hasCutoutOppo(context)) {
            return 80;
        }
        if (hasCutoutHW(context)) {
            int height = getCutoutHW(context);
            if (height <= 0) {
                return getNormalHeight(context);
            }
            return height;
        }
        if (hasCutoutVivo(context)) {
            return dip2px(30);
        }

        return getNormalHeight(context);
    }

    private static int getNormalHeight(Context context) {
        if (context == null) {
            return dip2px(24);
        }
        Rect localRect = new Rect();
        View decorView = ((Activity) context).getWindow().getDecorView();
        decorView.getWindowVisibleDisplayFrame(localRect);
        int statusHeight = localRect.top;
        if (0 == statusHeight) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            if (0 < resourceId) {
                statusHeight = resources.getDimensionPixelOffset(resourceId);
            } else {
                statusHeight = dip2px(24);//默认24
            }
        }
        return statusHeight;
    }

    private static boolean hasCutoutOppo(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen" +
                ".heteromorphism");
    }

    private static boolean hasCutoutVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class ftFeature = cl.loadClass("android.util.FtFeature");
            Method isFeatureSupport = ftFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) isFeatureSupport.invoke(ftFeature, NOTCH_IN_SCREEN_VIVO);
        } catch (ClassNotFoundException e) {
            LogUtils.e("hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            LogUtils.e("hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            LogUtils.e("hasNotchInScreen Exception");
        }
        return ret;
    }

    private static boolean hasCutoutHW(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.hwNotchSizeUtil");
            Method get = hwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(hwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            LogUtils.e("hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            LogUtils.e("hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            LogUtils.e("hasNotchInScreen Exception");
        }
        return ret;
    }

    private static int getCutoutHW(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = hwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(hwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            LogUtils.e("getNotchSize ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            LogUtils.e("getNotchSize NoSuchMethodException");
        } catch (Exception e) {
            LogUtils.e("getNotchSize Exception");
        }
        return ret[1];
    }
}

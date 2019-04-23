package com.jerry.wechatservice.util;

import android.os.Build;
import android.view.ViewConfiguration;
import com.jerry.wechatservice.BuildConfig;

/**
 * 常用方法的工具类
 *
 * @author my
 * @time 2016/9/22 14:41
 */
public class AppUtils {

    private static long lastClickTime;

    /**
     * 获取应用版本号
     *
     * @return 应用版本号
     * @throws Exception
     */
    public static int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 是否快速点击
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < ViewConfiguration.getJumpTapTimeout()) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}

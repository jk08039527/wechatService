package com.jerry.wechatservice.util;

import android.text.TextUtils;
import android.widget.Toast;
import com.jerry.wechatservice.MyApplication;

/**
 * 自定义的Toast工具类，为了避免用户不间断点击
 * 也可以显示自定义的Toast
 *
 * @author my
 * @time 2016/9/22 14:44
 */
public class ToastUtil {

    private static Toast toast = null;

    /**
     * 显示Toast提示
     *
     * @param s        字符串内容
     * @param duration 显示时间
     */
    public static void showText(String s, int duration) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        if (toast != null) {
            toast.cancel();
        }

        //环境变量不为空
        toast = Toast.makeText(MyApplication.getInstance(), s, duration);
        toast.show();
    }

    /**
     * 显示Toast提示
     *
     * @param resId    字符串资源Id
     * @param duration 显示时间
     */
    public static void showText(int resId, int duration) {
        if (resId == 0) {
            return;
        }
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(MyApplication.getInstance(), resId, duration);
        toast.show();
    }

    /**
     * 显示Toast提示，显示时间默认为Toast.LENGTH_LONG
     *
     * @param s       字符串内容
     */
    public static void showLongText(String s) {
        showText(s, Toast.LENGTH_LONG);
    }

    /**
     * 显示Toast提示，显示时间默认为Toast.LENGTH_LONG
     *
     * @param resId   字符串资源Id
     */
    public static void showLongText(int resId) {
        showText(resId, Toast.LENGTH_LONG);
    }

    /**
     * 显示Toast提示，显示时间默认为Toast.LENGTH_SHORT
     *
     * @param s       字符串内容
     */
    public static void showShortText(String s) {
        showText(s, Toast.LENGTH_SHORT);
    }

    /**
     * 显示Toast提示，显示时间默认为Toast.LENGTH_SHORT
     *
     * @param resId   字符串资源Id
     */
    public static void showShortText(int resId) {
        showText(resId, Toast.LENGTH_SHORT);
    }

}

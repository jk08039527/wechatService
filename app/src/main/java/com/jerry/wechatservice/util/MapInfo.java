package com.jerry.wechatservice.util;

import java.util.LinkedList;

/**
 * @author Jerry
 * @createDate 2019/4/10
 * @copyright www.axiang.com
 * @description 单条记录
 */
public class MapInfo {

    public int loaded;
    public LinkedList<StrInfo> strInfo = new LinkedList<>();

    public static class StrInfo {
        /**
         * 时间
         */
        public String time;
        /**
         * 内容
         */
        public String content;
    }
}

package com.jerry.wechatservice.accessibility;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Binder;
import android.util.ArrayMap;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jerry.wechatservice.bean.Record;
import com.jerry.wechatservice.util.CollectionUtils;
import com.jerry.wechatservice.util.DateUtils;
import com.jerry.wechatservice.util.FileUtil;
import com.jerry.wechatservice.util.GBData;
import com.jerry.wechatservice.util.ListCacheUtil;
import com.jerry.wechatservice.util.MapInfo;
import com.jerry.wechatservice.util.WeakHandler;

/**
 * Created by cxk on 2017/2/4.
 * email:471497226@qq.com
 * <p>
 * 获取即时微信聊天记录服务类
 */

public class WeChatLogService extends AccessibilityService {

    private long time;
    private static final int MAP_DATA = 1;
    private static final int RECORD_DATA = 2;
    private static final int NULLTIME = 5000;
    /**
     * 聊天对象
     */
    private ArrayMap<String, MapInfo> map = new ArrayMap<>();
    private WeakHandler weakHandler = new WeakHandler(msg -> {
        switch (msg.what) {
            case MAP_DATA:
                this.weakHandler.sendEmptyMessageDelayed(RECORD_DATA, NULLTIME);
                break;
            case RECORD_DATA:
                StringBuilder sb = new StringBuilder();
                sb.append(DateUtils.getTimeByLong(time)).append("_");
                time = System.currentTimeMillis();
                sb.append(DateUtils.getTimeByLong(time));
                ListCacheUtil.saveValueToJsonFile(sb.toString(), JSON.toJSONString(map));
                this.weakHandler.sendEmptyMessageDelayed(RECORD_DATA, NULLTIME);
                break;
            default:
                break;
        }
        return false;
    });

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            //每次在聊天界面中有新消息到来时都出触发该事件
            case AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_TITLE:
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                //获取当前聊天页面的根布局
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                //获取聊天信息
                getWeChatLog(rootNode);
                break;
            default:
                break;
        }
    }

    /**
     * 遍历
     *
     * @param rootNode
     */

    private void getWeChatLog(AccessibilityNodeInfo rootNode) {
        if (rootNode != null) {
            // 和谁聊天
            List<AccessibilityNodeInfo> title = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/k3");
            if (CollectionUtils.isEmpty(title)) {
                return;
            }
            String name = title.get(0).getText().toString();
            // 去掉正在输入的情况
            if (name.contains("输入")) {
                return;
            }
            // 聊天item
            List<AccessibilityNodeInfo> aas = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aa");
            if (CollectionUtils.isEmpty(aas)) {
                return;
            }
            AccessibilityNodeInfo aa = aas.get(0);
            if (aa == null || aa.getChildCount() < 1) {
                return;
            }
            // 寻找listview 确定item的数量 如果和上次一样或者变化太大就不处理
            AccessibilityNodeInfo list = aa.getParent();
            while (list != null) {
                if ("android.widget.ListView".equals(list.getClassName().toString())) {
                    break;
                }
                list = list.getParent();
            }
            if (list == null) {
                return;
            }
            AccessibilityNodeInfo.CollectionInfo info = list.getCollectionInfo();
            int count = info.getRowCount();
            MapInfo mapInfo = map.get(name);
            if (mapInfo == null) {
                mapInfo = new MapInfo();
                mapInfo.loaded = count;
                map.put(name, mapInfo);
            } else {
                if (count == mapInfo.loaded || Math.abs(count - mapInfo.loaded) > 5) {
                    mapInfo.loaded = count;
                    return;
                }
            }
            mapInfo.loaded = count;
            // 拿到最后一个item
            AccessibilityNodeInfo last = aas.get(aas.size() - 1);
            AccessibilityNodeInfo u = last.getParent();
            // 拿到时间
            List<AccessibilityNodeInfo> ags = u.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ag");
            final MapInfo.StrInfo strInfo = new MapInfo.StrInfo();
            if (!CollectionUtils.isEmpty(ags)) {
                strInfo.time = ags.get(0).getText().toString();
            }
            // 拿到内容
            List<AccessibilityNodeInfo> nus = u.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/nu");
            if (CollectionUtils.isEmpty(nus)) {
                return;
            }
            //获取最后一行聊天的线性布局（即是最新的那条消息）
            AccessibilityNodeInfo finalNode = nus.get(0);
            Rect rect = new Rect();
            finalNode.getBoundsInScreen(rect);
            if (GBData.getPic(rect.left, rect.top, rect.width(), rect.height())) {
                final MapInfo finalMapInfo = mapInfo;
                RecognizeService.recAccurateBasic(this, FileUtil.getSaveFile().getAbsolutePath(), result -> {
                    Log.d("eee", result);
                    try {
                        Record record = JSON.parseObject(result, Record.class);
                        List<Record.WordsResultBean> wordsResultBeans;
                        if (record != null) {
                            wordsResultBeans = record.getWords_result();
                            if (wordsResultBeans != null) {
                                StringBuilder sb = new StringBuilder();
                                for (Record.WordsResultBean wordsResultBean : wordsResultBeans) {
                                    sb.append(wordsResultBean.getWords());
                                }
                                strInfo.content = sb.toString();
                                finalMapInfo.strInfo.add(strInfo);
                                weakHandler.sendEmptyMessage(MAP_DATA);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    /**
     * 必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
     */
    @Override
    public void onInterrupt() {
        Toast.makeText(this, "我快被终结了啊-----", Toast.LENGTH_SHORT).show();
    }

    /**
     * 服务开始连接
     */
    @Override
    protected void onServiceConnected() {
        Toast.makeText(this, "服务已开启", Toast.LENGTH_SHORT).show();
        time = System.currentTimeMillis();
        super.onServiceConnected();
    }

    /**
     * 服务断开
     *
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "服务已被关闭", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    public class PlayBinder extends Binder {
        public WeChatLogService getService() {
            return WeChatLogService.this;
        }
    }
}
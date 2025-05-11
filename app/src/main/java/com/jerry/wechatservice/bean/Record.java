package com.jerry.wechatservice.bean;

/**
 * @author Jerry
 * @createDate 2019/4/10
 * @copyright www.axiang.com
 * @description 一条记录
 */
public class Record {
    private String msgId;
    private String content;
    private String createTime;
    private int isSend;
    private String nickname;
    private String talker;
    private String username;

    public Record() {
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getContent() {
        return this.content;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public int getIsSend() {
        return this.isSend;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getTalker() {
        return this.talker;
    }

    public String getUsername() {
        return this.username;
    }

    public void setContent(String var1) {
        this.content = var1;
    }

    public void setCreateTime(String var1) {
        this.createTime = var1;
    }

    public void setIsSend(int var1) {
        this.isSend = var1;
    }

    public void setNickname(String var1) {
        this.nickname = var1;
    }

    public void setTalker(String var1) {
        this.talker = var1;
    }

    public void setUsername(String var1) {
        this.username = var1;
    }
}

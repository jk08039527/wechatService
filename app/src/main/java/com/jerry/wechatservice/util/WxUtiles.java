package com.jerry.wechatservice.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.jerry.wechatservice.asyctask.AsycTask;
import com.jerry.wechatservice.bean.Record;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WxUtiles {
    private static SQLiteDatabaseHook hook;
    private static final List<File> mWxDbPathList = new ArrayList<>();

    public WxUtiles() {
    }

    public static SQLiteDatabase getWxDB(File file, Context context, String name) {
        SQLiteDatabase.loadLibs(context);
        hook = new SQLiteDatabaseHook() {
            public void postKey(SQLiteDatabase var1) {
                var1.rawExecSQL("PRAGMA cipher_migrate;");
            }

            public void preKey(SQLiteDatabase var1) {
            }
        };
        return SQLiteDatabase.openOrCreateDatabase(file, name, null, hook);
    }

    /**
     * 递归查询微信本地数据库文件
     *
     * @param file     目录
     * @param fileName 需要查找的文件名称
     */
    public static void searchFile(File file, String fileName) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File childFile : files) {
                    searchFile(childFile, fileName);
                }
            }
        } else {
            if (fileName.equals(file.getName())) {
                mWxDbPathList.add(file);
            }
        }
    }

    /**
     * 查询聊天信息
     * 这里查出的聊天信息包含用户主动删除的信息
     * 无心的聊天信息删除不是物理删除，所哟只要不卸载仍然可以查到聊天记录
     *
     * @param db
     */
    public static void getMessageData(SQLiteDatabase db, String username, WxUtiles.DataCallback<Record> callback) {
        AsycTask.withoutContext().assign(() -> {
            List<Record> records = new ArrayList<>();
            Cursor c1 = null;
            try {
                //这里只查询文本消息，type=1  图片消息是47，具体信息可以自己测试  http://emoji.qpic.cn/wx_emoji/gV159fHh6rYfCMejCAU1wIoP6eywxFMYjaJiaBzPbSjoc6XlTLoMyKQEh4nswfrX5/ （发送表情连接可以拼接的）
                c1 = db.rawQuery("select * from message where type = 1 and talker = '" + username + "'", null);
                while (c1.moveToNext()) {
                    Record record = new Record();
                    record.setMsgId(c1.getString(c1.getColumnIndex("msgId")));
                    record.setTalker(c1.getString(c1.getColumnIndex("talker")));
                    record.setContent(c1.getString(c1.getColumnIndex("content")));
                    record.setCreateTime(c1.getString(c1.getColumnIndex("createTime")));
                    record.setIsSend(c1.getInt(c1.getColumnIndex("isSend")));
                    records.add(record);
                }
                FileUtil.close(c1);
            } catch (Exception e) {
                FileUtil.close(c1);
            }
            return records;
        }).whenDone(result -> {
            if (callback != null) {
                if (result == null) {
                    callback.onError("聊天信息查询失败");
                } else {
                    callback.onResult((List<Record>) result);
                }
            }
        }).execute();
    }

    public static void getRecontactData(SQLiteDatabase db, WxUtiles.DataCallback<Record> callback) {
        AsycTask.withoutContext().assign(() -> {
            List<Record> records = new ArrayList<>();
            Cursor c1 = null;
            try {
                //查询所有联系人（verifyFlag!=0:公众号等类型，群里面非好友的类型为4，未知类型2）
                c1 = db.rawQuery(
                        "select * from rcontact where verifyFlag = 0 and type != 4 and type != 2 and nickname != ''",
                        null);
                while (c1.moveToNext()) {
                    Record record = new Record();
                    record.setUsername(c1.getString(c1.getColumnIndex("username")));
                    record.setNickname(c1.getString(c1.getColumnIndex("nickname")));
                    records.add(record);
                }
                FileUtil.close(c1);
            } catch (Exception e) {
                FileUtil.close(c1);
                Log.e("openWxDb", "读取数据库信息失败" + e.toString());
            }
            return records;
        }).whenDone(result -> {
            if (callback != null) {
                if (result == null) {
                    callback.onError("读取数据库信息失败");
                } else {
                    callback.onResult((List<Record>) result);
                }
            }
        }).execute();
    }

    public static void openWxDb(File file, Context context, String name) {
        SQLiteDatabase.loadLibs(context);
        hook = new SQLiteDatabaseHook() {
            public void postKey(SQLiteDatabase db) {
                db.rawExecSQL("PRAGMA cipher_migrate;");
            }

            public void preKey(SQLiteDatabase db) {
            }
        };
        runRecontact(context, SQLiteDatabase.openOrCreateDatabase(file, name, null, hook));
    }


    private static void runMessage(Context mContext, SQLiteDatabase db) {
        AsycTask.withoutContext().assign(() -> {
            Toast.makeText(mContext, "聊天信息查询完毕", Toast.LENGTH_LONG).show();
            runChatRoom(mContext, db);
            return true;
        }).execute();
    }

    /**
     * 获取群聊成员列表
     *
     * @param mContext
     * @param db
     */
    private static void runChatRoom(Context mContext, SQLiteDatabase db) {
        getChatRoomDate(mContext, db);
    }

    /**
     * 获取群聊成员列表
     *
     * @param mContext
     * @param db
     */
    private static void getChatRoomDate(Context mContext, SQLiteDatabase db) {
        Cursor c1 = null;
        try {
            c1 = db.rawQuery("select * from chatroom ", null);
            Log.e("openWxDb", "群组信息记录分割线=====================================================================================");
            while (c1.moveToNext()) {
                String roomowner = c1.getString(c1.getColumnIndex("roomowner"));
                String chatroomname = c1.getString(c1.getColumnIndex("chatroomname"));
                String memberlist = c1.getString(c1.getColumnIndex("memberlist"));
                Log.e("openWxDb", "群主====" + roomowner + "    群组成员id=====" + memberlist + "    群id=====" + chatroomname);
            }
            c1.close();
            db.close();
        } catch (Exception e) {
            c1.close();
            db.close();
            Log.e("openWxDb", "读取数据库信息失败" + e.toString());
        }
    }

    /**
     * 查询聊天信息
     * 这里查出的聊天信息包含用户主动删除的信息
     * 无心的聊天信息删除不是物理删除，所哟只要不卸载仍然可以查到聊天记录
     *
     * @param db
     */
    private static void getMessageData(SQLiteDatabase db) {
        Cursor c1 = null;
        try {
            //这里只查询文本消息，type=1  图片消息是47，具体信息可以自己测试  http://emoji.qpic.cn/wx_emoji/gV159fHh6rYfCMejCAU1wIoP6eywxFMYjaJiaBzPbSjoc6XlTLoMyKQEh4nswfrX5/ （发送表情连接可以拼接的）
            c1 = db.rawQuery("select * from message where type = 1 ", null);
            Log.e("openWxDb", "聊天记录分割线=====================================================================================");
            while (c1.moveToNext()) {
                String talker = c1.getString(c1.getColumnIndex("talker"));
                String content = c1.getString(c1.getColumnIndex("content"));
                String createTime = c1.getString(c1.getColumnIndex("createTime"));
                Log.e("openWxDb", "聊天对象微信号====" + talker + "    内容=====" + content + "    时间=====" + createTime);
            }
            c1.close();
        } catch (Exception e) {
            c1.close();
            Log.e("openWxDb", "读取数据库信息失败" + e.toString());
        }
    }

    /**
     * 微信好友信息
     *
     * @param mContext
     * @param db
     */
    private static void runRecontact(Context mContext, SQLiteDatabase db) {
        AsycTask.withoutContext().assign(() -> {
            Toast.makeText(mContext, "查询通讯录完毕", Toast.LENGTH_LONG).show();
            runMessage(mContext, db);
            return true;
        }).execute();
    }

    /**
     * 获取当前用户的微信所有联系人
     */
    private static void getRecontactDate(SQLiteDatabase db) {
        Cursor c1 = null;
        try {
            //查询所有联系人（verifyFlag!=0:公众号等类型，群里面非好友的类型为4，未知类型2）
            c1 = db.rawQuery(
                    "select * from rcontact where verifyFlag = 0 and type != 4 and type != 2 and nickname != ''",
                    null);
            while (c1.moveToNext()) {
                String userName = c1.getString(c1.getColumnIndex("username"));
                String nickName = c1.getString(c1.getColumnIndex("nickname"));
                Log.e("openWxDb", "userName====" + userName + "    nickName=====" + nickName);
            }
            c1.close();
        } catch (Exception e) {
            c1.close();
            Log.e("openWxDb", "读取数据库信息失败" + e.toString());
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteRead = 0;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
        } catch (Exception e) {
            Log.e("copyFile", "复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    public interface DataCallback<T> {
        void onError(String error);

        void onResult(List<T> records);
    }
}
package com.jerry.wechatservice;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jerry.wechatservice.asyctask.AsycTask;
import com.jerry.wechatservice.bean.Record;
import com.jerry.wechatservice.ptrlib.BaseRecyclerAdapter;
import com.jerry.wechatservice.ptrlib.widget.PtrRecyclerView;
import com.jerry.wechatservice.util.DecryptUtiles;
import com.jerry.wechatservice.util.WxUtiles;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    protected BaseRecyclerAdapter<Record> mAdapter;
    protected List<Record> mData = new ArrayList<>();
    protected PtrRecyclerView mPtrRecyclerView;
    private String talker;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        talker = getIntent().getStringExtra("talker");
        mAdapter = new RecordAdapter(this, mData);
        mPtrRecyclerView = new PtrRecyclerView(this);
        mPtrRecyclerView.setAdapter(this.mAdapter);
        setContentView(this.mPtrRecyclerView);
        connectDatabase();
    }

    private void connectDatabase() {
        AsycTask.with(this).assign(() -> {
            String password = DecryptUtiles.initDbPassword(ChatActivity.this);
            try {
                Log.e("path", password);
                //将微信数据库导出到sd卡操作sd卡上数据库
                db = WxUtiles.getWxDB(new File(DecryptUtiles.copyFilePath), ChatActivity.this, password);
            } catch (Exception e) {
                Log.e("path", e.getMessage());
                e.printStackTrace();
            }
            return true;
        }).whenDone(result -> {
            if (db != null) {
                WxUtiles.getMessageData(db, talker, new WxUtiles.DataCallback<Record>() {
                    @Override
                    public void onError(String var1) {
                        Toast.makeText(ChatActivity.this, "查询聊天记录失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResult(List<Record> records) {
                        mData.clear();
                        if (records != null) {
                            mData.addAll(records);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).execute();
    }
}
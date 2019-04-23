package com.jerry.wechatservice;

import android.content.Context;
import android.widget.TextView;

import com.jerry.wechatservice.bean.Record;
import com.jerry.wechatservice.ptrlib.BaseRecyclerAdapter;
import com.jerry.wechatservice.ptrlib.RecyclerViewHolder;

import java.util.List;

class RecordAdapter extends BaseRecyclerAdapter<Record> {
    public RecordAdapter(Context context, List<Record> data) {
        super(context, data);
    }

    public void convert(RecyclerViewHolder holder, int position, int viewType, Record bean) {
        TextView var5 = holder.getView(R.id.tv_talker);
        TextView var6 = holder.getView(R.id.tv_content);
        var6.setText(bean.getContent());
        if (bean.getIsSend() == 1) {
            var5.setGravity(8388613);
            var6.setGravity(8388613);
            var5.setText("我");
        } else {
            var5.setText(bean.getTalker());
            var5.setGravity(8388611);
            var6.setGravity(8388611);
        }
    }

    public int getItemLayoutId(int var1) {
        return R.layout.item_record;
    }
}
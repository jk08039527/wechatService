package com.jerry.wechatservice;

import android.content.Context;
import android.widget.TextView;
import com.jerry.wechatservice.bean.Record;
import com.jerry.wechatservice.ptrlib.BaseRecyclerAdapter;
import com.jerry.wechatservice.ptrlib.RecyclerViewHolder;

import java.util.List;

/**
 * @author Jerry
 * @createDate 2019-04-23
 * @copyright www.axiang.com
 * @description
 */
public class ContactAdapter extends BaseRecyclerAdapter<Record> {

    public ContactAdapter(Context context, List<Record> list) {
        super(context, list);
    }

    @Override
    public void convert(RecyclerViewHolder holder, int i, int i1, Record record) {
        TextView tvTalker = holder.getView(R.id.tv_talker);
        TextView tvContent = holder.getView(R.id.tv_content);
        tvTalker.setText(record.getUsername());
        tvContent.setText(record.getNickname());
    }

    @Override
    public int getItemLayoutId(int i) {
        return R.layout.item_record;
    }
}

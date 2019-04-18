package com.jerry.wechatservice

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView

class LogActivity : AppCompatActivity() {
    var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        recyclerView = findViewById(R.id.recyclerView)
    }
}

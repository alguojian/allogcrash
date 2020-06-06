package com.alguojian.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.alguojian.logcrash.LogCrashOpenUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var aa: List<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button2.setOnClickListener { v: View? ->
            for (i in 0..99) {
                aa!![1111]
            }
            val aa = "123"
            val bb = aa.substring(9, 123)
        }

        LogCrashOpenUtils.open(button,this@MainActivity)
    }
}
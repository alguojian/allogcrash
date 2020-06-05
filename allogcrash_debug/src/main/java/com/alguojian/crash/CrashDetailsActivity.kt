package com.alguojian.crash

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class CrashDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_details)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        val data = intent.getSerializableExtra("data") as CrashBean
        val textView = findViewById<TextView>(R.id.name)
        val textView1 = findViewById<TextView>(R.id.text)
        if (TextUtils.isEmpty(data.userId)) {
            textView.text = data.phoneName
        } else {
            textView.text = data.userId + data.phoneName
        }
        textView1.text = data.crash
    }

    companion object {
        fun start(context: Activity, crashBean: CrashBean?, cardView: LinearLayout?) {
            val starter = Intent(context, CrashDetailsActivity::class.java)
            starter.putExtra("data", crashBean)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.startActivity(starter, ActivityOptions.makeSceneTransitionAnimation(context, cardView, "jump").toBundle())
            } else {
                context.startActivity(starter)
            }
        }
    }
}
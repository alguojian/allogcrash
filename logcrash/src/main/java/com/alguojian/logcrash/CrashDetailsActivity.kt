package com.alguojian.logcrash

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.transition.TransitionInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_crash_details.*

class CrashDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            val explode = TransitionInflater.from(this).inflateTransition(android.R.transition.fade)
            window.enterTransition = explode
        }
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

        fab.setOnClickListener {
            var shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, data.crash)
            shareIntent = Intent.createChooser(shareIntent, "日志详情")
            startActivity(shareIntent)
        }
    }

    companion object {
        fun start(context: Activity, crashBean: CrashBean?) {
            val starter = Intent(context, CrashDetailsActivity::class.java)
            starter.putExtra("data", crashBean)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.startActivity(starter, ActivityOptions.makeSceneTransitionAnimation(context).toBundle())
            } else {
                context.startActivity(starter)
            }
        }
    }
}
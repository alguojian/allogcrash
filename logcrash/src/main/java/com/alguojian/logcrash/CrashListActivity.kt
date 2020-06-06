package com.alguojian.logcrash

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import org.litepal.LitePal.delete
import org.litepal.LitePal.deleteAll
import org.litepal.LitePal.order

class CrashListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            val explode = TransitionInflater.from(this).inflateTransition(android.R.transition.explode)
            window.enterTransition = explode
        }

        setContentView(R.layout.activity_crash_list)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val crashAdapter = CrashAdapter()
        recyclerView.adapter = crashAdapter
        crashAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        crashAdapter.isFirstOnly(false)
        crashAdapter.setNewData(order("time desc").find(CrashBean::class.java))
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refresh)
        swipeRefreshLayout.setColorSchemeColors(Color.RED)
        swipeRefreshLayout.setOnRefreshListener {
            crashAdapter.setNewData(order("time desc").find(CrashBean::class.java))
            swipeRefreshLayout.isRefreshing = false
        }
        crashAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, _, position ->
            CrashDetailsActivity.start(this@CrashListActivity, crashAdapter.data[position])
        }
        crashAdapter.onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { _, _, position ->
            AlertDialog.Builder(this@CrashListActivity)
                    .setTitle("提示")
                    .setMessage("确定要删除吗？")
                    .setNegativeButton("删除全部") { dialog, which ->
                        deleteAll(CrashBean::class.java)
                        crashAdapter.setNewData(null)
                    }
                    .setPositiveButton("删除该条") { dialog, which ->
                        val crashBean = crashAdapter.data[position]
                        val delete = delete(CrashBean::class.java, crashBean._id)
                        if (1 == delete) {
                            Toast.makeText(this@CrashListActivity, "删除成功", Toast.LENGTH_SHORT).show()
                        }
                        crashAdapter.remove(position)
                    }.create().show()
            true
        }
    }


    companion object {
        fun start(context: Activity) {
            val starter = Intent(context, CrashListActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.startActivity(starter, ActivityOptions.makeSceneTransitionAnimation(context).toBundle())
            } else {
                context.startActivity(starter)
            }
        }
    }
}
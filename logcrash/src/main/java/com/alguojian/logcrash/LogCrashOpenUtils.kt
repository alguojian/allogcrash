package com.alguojian.logcrash

import android.app.Activity
import android.view.View
import com.alguojian.logcrash.CrashListActivity

object LogCrashOpenUtils {

    /**
     * 正式环境下两秒内点击5次，打开bug列表
     */
    @JvmStatic
    fun open(view: View?, activity: Activity?) {

        if (view == null || activity == null)
            return

        var clickNum = 0
        var time = System.currentTimeMillis()

        view.setOnClickListener {

            if (System.currentTimeMillis() - time > 2000){
                clickNum = 0
                time=System.currentTimeMillis()
            }

            clickNum++

            if (clickNum >= 5 && (System.currentTimeMillis() - time) <= 2000) {
                CrashListActivity.start(activity)
                clickNum = 0
            }
        }
    }
}
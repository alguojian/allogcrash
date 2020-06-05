package com.alguojian.logcrash

import android.app.Application
import com.alguojian.crash.CrashHandler
import java.util.*

/**
 * ${Descript}
 *
 * @author alguojian
 * @date 2018/8/16
 */
class Mpapp : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashHandler.initThis(this)
        val treeMap = TreeMap<String, String?>()
        treeMap["用户手机号"] = null
        CrashHandler.setOtherNews(treeMap)
        CrashHandler.setDingDingLink("https://oapi.dingtalk.com/robot/send?access_token=9dd82a4b444089a65fcfbb522fa789709170db6142e64a832ff79c637f180533")
    }
}
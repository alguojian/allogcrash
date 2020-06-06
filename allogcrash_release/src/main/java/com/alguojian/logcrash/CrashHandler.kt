package com.alguojian.logcrash

import android.app.Application

object CrashHandler {
    @JvmStatic
    @JvmOverloads
    fun initThis(context: Application, alreadyUsed: Boolean = false) {
    }

    @JvmStatic
    fun setOtherNews(hashMap: HashMap<String, String?>) {

    }

    @JvmStatic
    fun setDingDingLink(s: String?) {
    }

    @JvmStatic
    fun postCrashToDingding() {
    }
}
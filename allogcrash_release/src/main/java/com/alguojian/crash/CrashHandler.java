package com.alguojian.crash;

import android.content.Context;

public class CrashHandler {

    private static CrashHandler INSTANCE = new CrashHandler();

    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }


    public void init(Context context) {
    }


    public void setOtherNews(Object otherNews) {
    }


    public void setDingDingLink(String s) {
    }

}
package com.alguojian.logcrash;

import android.app.Application;

import com.alguojian.crash.CrashHandler;

/**
 * ${Descript}
 *
 * @author alguojian
 * @date 2018/8/16
 */
public class Mpapp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getInstance().init(this);
    }
}

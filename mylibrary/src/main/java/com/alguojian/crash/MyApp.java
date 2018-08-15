package com.alguojian.crash;

import android.app.Application;

import org.litepal.LitePal;

/**
 * ${Descript}
 *
 * @author alguojian
 * @date 2018/8/15
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        CrashHandler.getInstance().init(this);
    }
}

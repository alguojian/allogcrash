package com.alguojian.logcrash;

import android.app.Application;

import com.alguojian.crash.CrashHandler;

import java.util.TreeMap;

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
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("用户手机号", null);
        CrashHandler.getInstance().setOtherNews(treeMap);
        CrashHandler.getInstance().setDingDingLink("https://oapi.dingtalk.com/robot/send?access_token=9dd82a4b444089a65fcfbb522fa789709170db6142e64a832ff79c637f180533");
    }
}

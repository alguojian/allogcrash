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
        treeMap.put("用户手机号","1111111111");
        CrashHandler.getInstance().setOtherNews(treeMap);
        CrashHandler.getInstance().setDingDingLink("https://oapi.dingtalk.com/robot/send?access_token=04c3473dd02444a631eaee0b30415d6c49b0f2ec25b6f755d56e15d606a322c0");
    }
}

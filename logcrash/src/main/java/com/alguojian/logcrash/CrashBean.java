package com.alguojian.logcrash;

import android.text.TextUtils;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ${Descript}
 *
 * @author alguojian
 * @date 2018/8/15
 */
public class CrashBean extends LitePalSupport implements Serializable {

    public long _id;
    public String crash;//crash信息
    public String phoneName;//手机品牌或者收集型号
    public String appVersion;//app版本号
    public long time;//发生crash时间
    public String userId;//用户iD

    @Override
    public String toString() {
        return TextUtils.isEmpty(userId) ? "" : userId + "\n" +
                phoneName +
                "\n时 间         : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time)) +
                "\n\n" + crash;
    }
}

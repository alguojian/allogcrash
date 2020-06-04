package com.alguojian.crash;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * app闪退日志记录
 *
 * @author alguojian
 * @date 2018/8/15
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private static CrashHandler INSTANCE = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    private StringBuffer mStringBuffer;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     */
    public void init(Application context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        LitePal.initialize(context);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     */
    private void collectDeviceInfo(Context ctx) {
        mStringBuffer = new StringBuffer(128);
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                mStringBuffer.append("APP版本：" + versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "-----------an error occured when collect package info-------------", e);
        }
        mStringBuffer
                .append("\n手机主板：").append(Build.BOARD)
                .append("\n系统厂商：").append(Build.BRAND)
                .append("\n系统版本：").append(Build.MODEL)
                .append("\n手机版本：").append(Build.PRODUCT);
    }

    /**
     * 保存错误信息到文件中
     */
    private void saveCrashInfo2File(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();

        CrashBean crashBean = new CrashBean();
        crashBean.phoneName = mStringBuffer.toString() + "\n触发时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        crashBean.appVersion = getVersion(mContext);
        crashBean.crash = result;
        crashBean.time = System.currentTimeMillis();
        crashBean.userId = LitePal.findFirst(OtherNewsBean.class).crash;
        crashBean.save();
    }

    // 取得版本号
    private String getVersion(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return manager.versionName;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * 设置一些其他信息，例如用户id等信息
     */
    public void setOtherNews(TreeMap<String, String> treeMap) {
        LitePal.deleteAll(OtherNewsBean.class);
        StringBuilder stringBuilder = new StringBuilder(128);

        for (Map.Entry<String, String> map : treeMap.entrySet()) {
            if (!TextUtils.isEmpty(map.getValue()))
                stringBuilder.append(map.getKey() + "：" + map.getValue() + "\n");
        }
        if (!TextUtils.isEmpty(stringBuilder.toString())) {
            OtherNewsBean otherNewsBean = new OtherNewsBean();
            otherNewsBean.crash = stringBuilder.toString();
            otherNewsBean.save();
        }
    }

    /**
     * 设置钉钉机器人链接,必须在设置其他信息之后才可以操作
     */
    public void setDingDingLink(String s) {
        OtherNewsBean first = LitePal.findFirst(OtherNewsBean.class);
        if (first == null) {
            OtherNewsBean otherNewsBean = new OtherNewsBean();
            otherNewsBean.dingding = s;
            otherNewsBean.save();
        }
    }

    public void postCrashToDingding() {
        CrashBean first = LitePal.findLast(CrashBean.class);

        if (first == null || first.crash == null) {
            return;
        }

        try {

            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("msgtype", "text");

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("content", first.toString());

            jsonObject.put("text", jsonObject1);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    URL url = null;
                    try {
                        String aaUrl = LitePal.findFirst(OtherNewsBean.class).dingding;
                        System.out.println("---------" + aaUrl);

                        url = new URL(aaUrl);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                        httpURLConnection.setRequestMethod("POST");

                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setDoOutput(true);

                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.connect();
                        OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
                        writer.write(jsonObject.toString());
                        writer.flush();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));

                        httpURLConnection.connect();
                        br.close();

                    } catch (Exception e) {
                        System.out.println("---------" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            System.out.println("---------" + e.getMessage());

            e.printStackTrace();
        }
    }
}
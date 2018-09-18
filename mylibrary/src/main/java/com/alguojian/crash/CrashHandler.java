package com.alguojian.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ${Descript}
 *
 * @author alguojian
 * @date 2018/8/15
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";
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
     *
     * @param context
     */
    public void init(Context context) {
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
     * @param ex
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
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {

        mStringBuffer = new StringBuffer(128);
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                mStringBuffer.append("APP版本：" + versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        mStringBuffer
                .append("\n手机主板：").append(Build.BOARD)
                .append("\n系统厂商：").append(Build.BRAND)
                .append("\n系统版本：").append(Build.MODEL)
                .append("\n手机版本：").append(Build.PRODUCT)
                .append("\n硬件序列号：").append(Build.SERIAL);
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

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
        crashBean.phoneName = mStringBuffer.toString();
        crashBean.appVersion = getVersion(mContext);
        crashBean.crash = result;
        crashBean.time = System.currentTimeMillis();
        crashBean.userId = LitePal.findFirst(OtherNewsBean.class).crash;
        crashBean.save();

        return null;
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
     *
     * @param treeMap
     */
    public void setOtherNews(TreeMap<String, String> treeMap) {

        LitePal.deleteAll(OtherNewsBean.class);
        OtherNewsBean otherNewsBean = new OtherNewsBean();
        StringBuilder stringBuilder = new StringBuilder(128);

        for (Map.Entry<String, String> map : treeMap.entrySet()) {
            stringBuilder.append(map.getKey() + "：" + map.getValue() + "\n");
        }
        otherNewsBean.crash = stringBuilder.toString();
        otherNewsBean.save();
    }

    /**
     * 设置钉钉机器人链接,必须在设置其他信息之后才可以操作
     */
    public void setDingDingLink(String s) {
        OtherNewsBean first = LitePal.findFirst(OtherNewsBean.class);
        first.dingding = s;
        first.update(first._id);
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

                        url = new URL(LitePal.findFirst(OtherNewsBean.class).dingding);
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
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
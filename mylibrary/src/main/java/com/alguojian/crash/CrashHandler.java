package com.alguojian.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import org.litepal.LitePal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

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
        } else {
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
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
                String versionCode = pi.versionCode + "";
                mStringBuffer.append("APP版本：" + versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        mStringBuffer
//                .append("\n手机主板：").append(Build.BOARD)
//                .append("\n系统启动程序版本号：").append(Build.BOOTLOADER)
                .append("\n系统厂商：").append(Build.BRAND)
//                .append("\ncpu指令集：").append(Build.CPU_ABI)
//                .append("\ncpu指令集2：").append(Build.CPU_ABI2)
//                .append("\n设置参数：").append(Build.DEVICE)
//                .append("\n显示屏参数：").append(Build.DISPLAY)
//                .append("\n无线电固件版本：").append(Build.getRadioVersion())
//                .append("\n硬件识别码：").append(Build.FINGERPRINT)
//                .append("\n硬件名称：").append(Build.HARDWARE)
//                .append("\nHOST:").append(Build.HOST)
//                .append("\n修订版本列表：").append(Build.ID)
//                .append("\n硬件制造商：").append(Build.MANUFACTURER)
                .append("\n系统版本：").append(Build.MODEL);
//                .append("\n硬件序列号：").append(Build.SERIAL)
//                .append("\n手机厂商：").append(Build.PRODUCT);
//                .append("\n描述Build的标签：").append(Build.TAGS)
//                .append("\nTIME:").append(Build.TIME)
//                .append("\nbuilder类型：").append(Build.TYPE)
//                .append("\nUSER:").append(Build.USER);
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
}
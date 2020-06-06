package com.alguojian.logcrash

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import org.json.JSONObject
import org.litepal.LitePal.deleteAll
import org.litepal.LitePal.findFirst
import org.litepal.LitePal.findLast
import org.litepal.LitePal.initialize
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * app闪退日志记录
 *
 * @author alguojian
 * @date 2018/8/15
 */

/**
 * 保证只有一个CrashHandler实例
 */
object CrashHandler : Thread.UncaughtExceptionHandler {
    private const val TAG = "CrashHandler"

    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
    private var mContext: Context? = null
    private var mStringBuffer: StringBuffer? = null

    /**
     * 初始化
     *
     * @param context     Application 对象
     * @param alreadyUsed 项目中是否已经使用LitePal
     */
    @JvmStatic
    @JvmOverloads
    fun initThis(context: Application, alreadyUsed: Boolean = false) {
        mContext = context
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
        if (!alreadyUsed) initialize(context)
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler!!.uncaughtException(thread, ex)
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        // 收集设备参数信息
        collectDeviceInfo(mContext)
        // 保存日志文件
        saveCrashInfo2File(ex)
        return true
    }

    /**
     * 收集设备参数信息
     */
    private fun collectDeviceInfo(ctx: Context?) {
        mStringBuffer = StringBuffer(128)
        try {
            val pm = ctx!!.packageManager
            val pi = pm.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                val versionName = if (pi.versionName == null) "null" else pi.versionName
                mStringBuffer!!.append("APP版本：$versionName")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            Log.e(TAG, "-----------an error occured when collect package info-------------", e)
        }
        mStringBuffer!!
                .append("\n手机主板：").append(Build.BOARD)
                .append("\n系统厂商：").append(Build.BRAND)
                .append("\n系统版本：").append(Build.MODEL)
                .append("\n手机版本：").append(Build.PRODUCT)
    }

    /**
     * 保存错误信息到文件中
     */
    private fun saveCrashInfo2File(ex: Throwable) {
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result = writer.toString()
        val crashBean = CrashBean()
        mStringBuffer!!
                .append("\n触发时间：").append("${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(System.currentTimeMillis()))}")
        crashBean.phoneName = mStringBuffer.toString().trim()
        crashBean.appVersion = getVersion(mContext)
        crashBean.crash = result
        crashBean.time = System.currentTimeMillis()
        crashBean.userId = findFirst(OtherNewsBean::class.java).crash
        crashBean.save()
    }

    /**
     * 获得版本号
     */
    private fun getVersion(context: Context?): String {
        return try {
            val manager = context!!.packageManager.getPackageInfo(
                    context.packageName, 0)
            manager.versionName
        } catch (e: Exception) {
            "Unknown"
        }
    }

    /**
     * 设置一些其他信息，例如用户id等信息
     */
    @JvmStatic
    fun setOtherNews(hashMap: HashMap<String, String?>) {
         deleteAll(OtherNewsBean::class.java)
        val stringBuilder = StringBuilder(128)
        for ((key, value) in hashMap) {
            if (!TextUtils.isEmpty(value))
                stringBuilder.append("""$key：$value""".trimIndent())
        }
        if (stringBuilder.toString().isNotEmpty()) {
            val otherNewsBean = OtherNewsBean()
            otherNewsBean.crash = stringBuilder.toString()
            otherNewsBean.save()
        }
    }

    /**
     * 设置钉钉机器人链接,必须在设置其他信息之后才可以操作
     */
    @JvmStatic
    fun setDingDingLink(s: String?) {
        val first: OtherNewsBean? = findFirst(OtherNewsBean::class.java)
        if (first == null) {
            val otherNewsBean = OtherNewsBean()
            otherNewsBean.dingding = s
            otherNewsBean.save()
        }
    }

    /**
     * 推送最新一条消息到钉钉群组
     */
    @JvmStatic
    fun postCrashToDingding() {
        val first: CrashBean? = findLast(CrashBean::class.java)
        if (first?.crash == null) {
            return
        }
        try {
            val jsonObject = JSONObject()
            jsonObject.put("msgtype", "text")
            val jsonObject1 = JSONObject()
            jsonObject1.put("content", first.toString())
            jsonObject.put("text", jsonObject1)
            Thread(Runnable {
                var url: URL? = null
                try {
                    val aaUrl = findFirst(OtherNewsBean::class.java).dingding
                    println("---------$aaUrl")
                    url = URL(aaUrl)
                    val httpURLConnection = url.openConnection() as HttpURLConnection
                    httpURLConnection.requestMethod = "POST"
                    httpURLConnection.doInput = true
                    httpURLConnection.doOutput = true
                    httpURLConnection.setRequestProperty("Content-Type", "application/json")
                    httpURLConnection.connect()
                    val writer = OutputStreamWriter(httpURLConnection.outputStream)
                    writer.write(jsonObject.toString())
                    writer.flush()
                    val br = BufferedReader(InputStreamReader(httpURLConnection.inputStream, "UTF-8"))
                    httpURLConnection.connect()
                    br.close()
                } catch (e: Exception) {
                    println("---------" + e.message)
                    e.printStackTrace()
                }
            }).start()
        } catch (e: Exception) {
            println("---------" + e.message)
            e.printStackTrace()
        }
    }
}
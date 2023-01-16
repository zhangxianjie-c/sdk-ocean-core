package com.ocean.core.framework.initialize.crash

import android.content.Context
import android.os.Looper
import com.ocean.core.BuildConfig

/**
Created by Zebra-RD张先杰 on 2022年4月2日09:46:03

Description:异常捕获处理类
 */
class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {
    companion object {
        private val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { CrashHandler() }

        @Synchronized
        fun create() = instance
    }

    //DEBUG是否启用
    var launchDebug = false
    var launchRelease = false
    private var mDefaultCrashHandler: Thread.UncaughtExceptionHandler? = null
    lateinit var context: Context


    //异常信息
    val errorList = ArrayList<String>()

    var isFirst:Boolean = true


    init {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        if (launchDebug && com.ocean.core.BuildConfig.BUILD_TYPE == "DEBUG" || com.ocean.core.BuildConfig.BUILD_TYPE == "RELEASE" && launchRelease) {
            Thread{
                Looper.prepare()
                addData(e)
                if (isFirst)
                    createAlert()
                Looper.loop()
            }.start()
        } else mDefaultCrashHandler!!.uncaughtException(t, e)
    }


    /**
     * 重启应用
     */
    fun restartApp() {
        // 重启应用
            context.startActivity(context.packageManager.getLaunchIntentForPackage(context.packageName))
        //干掉当前的程序
//        ActivityStack.create().finishAllActivity()
    }

    fun addData(e: Throwable){
        var recallList = ArrayList<String>()
        e.stackTrace.forEach {
            val substring = it.toString()
                .substring(it.toString().indexOf("(") + 1, it.toString().indexOf(")") - 1)
            val sp = substring.split(".")
            if (!recallList.contains(sp[0]))
                recallList.add(sp[0])
        }
        val recallString = StringBuilder()
        for (i in 0 until recallList.size) {
            recallString.append(recallList[i])
            if (i < recallList.size - 2)
                recallString.append("->")
        }
        val errorMessage: StringBuilder = StringBuilder()
        errorMessage.append("异常回溯：${recallString}\n")
        errorMessage.append("异常详情：${e.toString()}\n")
        e.stackTrace.forEach {
            errorMessage.append("${it}\n")
        }
        errorList.add(errorMessage.toString())
    }

    @Synchronized
    fun createAlert() {
//        isFirst = false
//        AlertDialog.Builder(ActivityStack.create().getShowActivity())
//            .setTitle("提示")
//            .setCancelable(false)
//            .setMessage("APP出现异常啦，上传信息帮助我们改进它~~~")
//            .setPositiveButton("上传并重启"
//            ) { dialog, which ->
//                createProgressAlert()
//            }
//            .setNegativeButton("重启") { dialog, which ->
//                restartApp()
//            }
//            .create()
//            .show()
    }

    private fun createProgressAlert() {
//        val progressDialog = ProgressDialog(ActivityStack.create().getShowActivity())
//        progressDialog.apply {
//            setTitle("正在上传")
//            setMessage("上传中")
//            setCancelable(false)
//            setProgressStyle(ProgressDialog.STYLE_SPINNER)
//        }
//        progressDialog.show()
//        val appName = "${SystemOS.appName} APP异常回执"
//        val errorMessage: StringBuilder = StringBuilder()
//        errorMessage.append("APP版本：${SystemOS.appName} - ${SystemOS.appVersion} - ${BuildConfig.BUILD_TYPE}\n\n")
//        errorMessage.append("手机型号：${SystemOS.brand} ${SystemOS.model}\n\n")
//        errorMessage.append("系统版本：Android ${android.os.Build.VERSION.RELEASE} / API ${android.os.Build.VERSION.SDK_INT}\n\n")
//        errorMessage.append("Linux内核版本：${System.getProperty("os.version")}\n\n")
//        errorMessage.append("基带版本：${android.os.Build.getRadioVersion()}\n\n")
//        errorMessage.append("编译版本号：${android.os.Build.DISPLAY}\n\n")
//        errorMessage.append("编译序列：${android.os.Build.FINGERPRINT}\n\n")
//       for(i in errorList.indices){
//           errorMessage.append("异常${i+1}：\n")
//           errorMessage.append("${errorList[i]}\n")
//       }
//        MailUtils.create().sendMail("xianjie.zhang@zebra-c.com", appName, errorMessage.toString()) {
//                progressDialog.dismiss()
//                restartApp()
//        }
    }
}

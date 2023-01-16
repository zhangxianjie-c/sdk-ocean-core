package com.ocean.core.framework

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Build.VERSION_CODES.S_V2
import android.provider.Settings
import android.util.Log


/**
Created by Zebra-RD张先杰 on 2022年7月6日15:02:30

Description:存放系统级或系统相关的信息
 */
object SystemOS{
    var density: Float = 0f
    var widthPixels: Int = 0
    var heightPixels: Int = 0
    // 设备名称
    var deviceName: String = ""
    // 手机品牌
    var brand: String = ""
    // 手机型号
    var model: String = ""
    // 操作系统
    var system: String = ""
    // 操作系统版本号
    var sysVersion: String = ""
    // 手机电量
    var battery: String = ""
    // app名称
    var appName: String = ""
    // app包名称
    var appPacketName: String = ""
    // app 版本
    var appVersion: String = ""
    // app 构建号
    var appBuildCode: String = ""
    // 运行环境
    @Deprecated("未赋值的字段")
    var env: String = ""
    /**
     * 实时获取电量
     */
    private fun getSystemBattery(context: Context): Int {
        val batteryInfoIntent: Intent = context.applicationContext.registerReceiver(null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED))!!
        val level = batteryInfoIntent.getIntExtra("level", 0)
        val batterySum = batteryInfoIntent.getIntExtra("scale", 100)
        return 100 * level / batterySum
    }

    /**
     * 初始化数据
     */
    fun initialize(context:Context){
        val displayMetrics = context.resources.displayMetrics
        density = displayMetrics.density
        widthPixels = displayMetrics.widthPixels
        heightPixels = displayMetrics.heightPixels
        deviceName = if(Build.VERSION.SDK_INT <= S_V2 &&Settings.Secure.getString(context.contentResolver, "bluetooth_name")!=null)Settings.Secure.getString(context.contentResolver, "bluetooth_name") else ""
        brand = android.os.Build.BRAND
        model = android.os.Build.MODEL
        system = android.os.Build.DEVICE
        sysVersion = "Android" + android.os.Build.VERSION.RELEASE
        battery = getSystemBattery(context).toString()
        appPacketName = context.applicationInfo.packageName
        appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        Log.i("SystemOS", "initialize success :\n displayMetrics: ${displayMetrics} \n density: ${density} \n widthPixels: ${widthPixels} \n heightPixels: ${heightPixels} \n deviceName: ${deviceName} \n brand ${brand} \n model: ${model} \n system ${system} \n sysVersion: ${sysVersion} \n battery: ${battery} \n appPacketName: ${appPacketName} \n appVersion: ${appVersion} \n appBuildCode: ${appBuildCode} \n env: ${env  }" )
    }

    private fun Context.isDebug(): Boolean {
        return this.applicationInfo != null &&
                this.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
}
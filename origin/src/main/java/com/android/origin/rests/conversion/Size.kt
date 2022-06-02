package com.android.origin.rests.conversion

import android.content.Context

/**
Created by Zebra-RD张先杰 on 2022年6月2日15:23:42

Description:
 */
object Size {
    /**
     * dp 转 px
     * @param dpValue
     * @return
     */
    fun Context.dp2px(dpValue: Float): Int {
        val scale: Float = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px 转 dp
     * @param pxValue
     * @return
     */
    fun Context.px2dp(pxValue: Float): Int {
        val scale: Float = resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * sp 转 px
     * @param spValue
     * @return
     */
    fun Context.sp2px(spValue: Float): Int {
        val fontScale: Float = resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * px 转 sp
     * @param pxValue
     * @return
     */
    fun Context.px2sp(pxValue: Float): Int {
        val fontScale: Float = resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }
}
package com.zebra.android.origin.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.lang.RuntimeException

/**
Created by Zebra-RD张先杰 on 2022年5月31日11:36:20

Description:
 */
class ZGlide {
    companion object {
        private var mDefaultImage: Drawable? = null

        fun ImageView.load(url: String, defaultImage: Drawable? = null) {
            Glide.with(this).load(when {
                url.contains("http") -> url
                defaultImage != null -> defaultImage
                mDefaultImage != null -> mDefaultImage
                else -> throw RuntimeException("图片路径不合法，并未设置占位图")
            }).into(this)
        }
    }

    /**
     * 设置默认的占位图
     * @param defaultImage Drawable对象
     */
    fun setDefaultImage(defaultImage: Drawable) {
        mDefaultImage = defaultImage
    }

    /**
     * 获取当前占位图
     * @return 当前占位图Drawable对象
     */
    fun getDefaultImage(): Drawable? {
        return mDefaultImage
    }
}
package com.android.origin.rests.glide

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.lang.RuntimeException

/**
Created by Zebra-RD张先杰 on 2022年5月31日11:36:20

Description:简化Glide
 */
fun Context.glide(init:GlideExtend.()->Unit){
        val glide = GlideExtend.create()
        init.invoke(glide)
}
class GlideExtend private constructor(){
    companion object {
        private val instant by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { GlideExtend() }
        fun create() = instant
        fun ImageView.load(url: String, defaultImage: Drawable? = null) {
            Glide.with(this).load(when {
                url.contains("http") -> url
                defaultImage != null -> defaultImage
                else -> throw RuntimeException("图片路径不合法，并未设置占位图")
            }).into(this)
        }
    }
}
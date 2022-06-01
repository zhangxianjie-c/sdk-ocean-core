package com.zebra.android.origin.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
Created by Zebra-RD张先杰 on 2022年5月31日11:36:20

Description:
 */
class ZGlide {
    companion object{
        fun ImageView.load(url:String, defaultImage:Drawable){
            if (url.contains("http")) Glide.with(this).load(url).into(this)
            else Glide.with(this).load(defaultImage)
        }
    }
}
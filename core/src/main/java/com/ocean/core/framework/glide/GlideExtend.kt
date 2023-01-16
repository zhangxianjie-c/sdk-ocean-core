package com.ocean.core.framework.glide

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
Created by Zebra-RD张先杰 on 2022年5月31日11:36:20

Description:简化Glide
 */
object GlideExtend {
    fun getGlideRequestOptions():RequestOptions=
        RequestOptions()
            .override(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .skipMemoryCache(true)
}
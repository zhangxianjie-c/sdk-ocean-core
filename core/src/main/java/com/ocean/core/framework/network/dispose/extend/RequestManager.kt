package com.ocean.core.framework.network.dispose.extend

import com.ocean.core.framework.network.dispose.pack.Response

/**
Created by Zebra-RD张先杰 on 2022年7月12日14:32:09

Description:请求时的处理
 */
class RequestManager(private val request: suspend () -> Response) {
    suspend fun request(
        onSuccess: (suspend Response.() -> Unit),
        onError: (suspend Throwable.() -> Unit),
    ) {
        kotlin.runCatching {
                request.invoke()
        }.onFailure {
            it.printStackTrace()
            onError.invoke(it)
        }.onSuccess {
            onSuccess.invoke(it)
        }
    }
}
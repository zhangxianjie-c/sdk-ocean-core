package com.ocean.core.framework.network.dispose.extend

import com.ocean.core.framework.network.exception.NetWorkException

/**
Created by Zebra-RD张先杰 on 2022年7月12日14:36:37

Description:有一说一，写注释真麻烦
 */
interface IResponse {
    suspend fun onLoading()

    suspend fun onTimeOut()

    suspend fun onError(exception: NetWorkException)

    suspend fun onSuccess(data: String,message: String?)
}
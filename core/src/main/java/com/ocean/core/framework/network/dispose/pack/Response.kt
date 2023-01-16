package com.ocean.core.framework.network.dispose.pack

/**
Created by Zebra-RD张先杰 on 2022年7月5日10:58:44

Description:数据脱壳，Retrofit的请求返回值是通过字段名和属性名匹配赋值的。
 */
 class Response() {
    var errcode: Int = 400
    var message: String? = null
    var data: String? = null
    fun isSuccess(): Boolean = errcode == 0
}


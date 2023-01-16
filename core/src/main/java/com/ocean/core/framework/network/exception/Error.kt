package com.ocean.core.framework.network.exception

/**
Created by Zebra-RD张先杰 on 2022年8月4日10:50:58

Description:
 */
enum class Error(private val code: Int, private val err: String) {

    /**
     * Result或Throwable为Null
     */
    RESULT_NULL(-1000,"Result is Null"),
    THROWABLE_NULL(-2000,"Throwable is Null"),
    /**
     * 未知错误
     */
    UNKNOWN(1000, "请求失败，请稍后再试"),
    /**
     * 解析错误
     */
    PARSE_ERROR(1001, "解析错误，请稍后再试"),
    /**
     * 网络错误
     */
    NETWORK_ERROR(1002, "网络连接错误，请稍后重试"),

    /**
     * 证书出错
     */
    SSL_ERROR(1004, "证书出错，请稍后再试"),

    /**
     * 连接超时
     */
    TIMEOUT_ERROR(1006, "网络连接超时，请稍后重试"),
    /**
     * 令牌过期
     */
    NETWORK_TOKEN_OUT(1007, "登录过期，请重新登录"),
    /**
     * 令牌过期
     */
    NETWORK_ERROR_0(1008, "服务端错误");
    fun getError(): String {
        return err
    }

    fun getCode(): Int {
        return code
    }

}
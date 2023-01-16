package com.ocean.core.framework.network.dispose.pack

import com.ocean.core.framework.network.exception.NetWorkException

/**
Created by Zebra-RD张先杰 on 2022年7月5日15:05:12

Description:对返回数据进行的包装，主要用于区分请求与响应的状态
 */
class Result(
    var status: String,
    //data部分
    var response: String = "",
    //异常
    var exception: NetWorkException = NetWorkException(),
    //message部分
    var message:String = ""
) {

    companion object {

        const val Loading = "Loading"
        const val Success = "Success"
        const val Failure = "Failure"
        const val TimeOut = "TimeOut"

    }

}
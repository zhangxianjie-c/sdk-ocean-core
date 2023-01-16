package com.ocean.core.framework.network.exception

import android.net.ParseException
import android.util.Log
import android.util.MalformedJsonException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException

/**
Created by Zebra-RD张先杰 on 2022年8月4日10:21:33

Description:这是一个自定义异常类 可能是网络异常 也可能是网络异常
 */
class NetWorkException:Exception {
    var errCode: Int = 0 //错误码
    var errorMsg:Error? = null
    var throwable: Throwable? = null
    constructor()
    constructor(errCode:Int,errorMsg:String):super(errorMsg){
        this.errCode = errCode
    }
    constructor(errCode:Int,errorMsg:String,throwable: Throwable):super(errorMsg){
        this.errCode = errCode
        this.throwable = throwable
    }
    constructor(error:Error,throwable: Throwable?):super(error.getError()){
        this.errCode = error.getCode()
        this.errorMsg = error
        this.throwable = throwable
    }
    companion object{
        fun transitionException(e: Throwable?):NetWorkException {
            e?.let {
                Log.e("NETWORK", "Exception: ${it}", )
                return when (it) {
                    is HttpException -> {
                        if (it.response()?.code() == 419)NetWorkException(Error.NETWORK_TOKEN_OUT,e)
                        else if(it.response()?.code() == 500)NetWorkException(Error.NETWORK_ERROR_0,e)
                        else NetWorkException(Error.NETWORK_ERROR,it)
                    }
                    is JSONException, is ParseException, is MalformedJsonException -> {
                        NetWorkException(Error.PARSE_ERROR,it)
                    }
                    is ConnectException -> {
                        NetWorkException(Error.NETWORK_ERROR,e)
                    }
                    is javax.net.ssl.SSLException -> {
                        NetWorkException(Error.SSL_ERROR,e)
                    }
                    is ConnectTimeoutException -> {
                        NetWorkException(Error.TIMEOUT_ERROR,e)
                    }
                    is java.net.SocketTimeoutException -> {
                        NetWorkException(Error.TIMEOUT_ERROR,e)
                    }
                    is java.net.UnknownHostException -> {
                        NetWorkException(Error.TIMEOUT_ERROR,e)
                    }
                    is NetWorkException -> return it

                    else -> {
                        NetWorkException(Error.UNKNOWN,e)
                    }
                }
            }
            return NetWorkException(Error.THROWABLE_NULL,e)
        }
    }

}
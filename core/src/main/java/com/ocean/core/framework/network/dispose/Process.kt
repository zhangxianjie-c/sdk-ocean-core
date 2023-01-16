package com.ocean.core.framework.network.dispose

import androidx.lifecycle.*
import com.ocean.core.framework.network.dispose.extend.RequestManager
import com.ocean.core.framework.network.dispose.extend.ResponseManager
import com.ocean.core.framework.network.dispose.pack.Response
import com.ocean.core.framework.network.dispose.pack.Result
import com.ocean.core.framework.network.exception.Error
import com.ocean.core.framework.network.exception.NetWorkException
import com.ocean.core.framework.network.getJSONFields
import kotlinx.coroutines.*

/**
Created by Zebra-RD张先杰 on 2022年7月12日10:22:01

Description:对请求和响应最主要的处理
 */
//这一步是把请求和响应的处理封装一下
fun request(data: MutableLiveData<Result>, request: suspend () -> Response) {
    //对request网络请求进行封装
    val requestManager = RequestManager(request)
    //对response返回类型的设置进行封装
    val responseManager = ResponseManager(data)
    //使用协程来进行线程控制
    GlobalScope.launch(Dispatchers.IO) {
        //把请求和响应的处理逻辑单独封装起来
        execute(requestManager, responseManager)
    }
}

//这一步是有真正执行请求的方法
private suspend  fun execute(
    requestManager: RequestManager,
    responseManager: ResponseManager,
) {
    responseManager.isReturn = false
    //若请求时间超过500毫秒，回调
    GlobalScope.launch(Dispatchers.IO) {
        delay(500)
        withContext(Dispatchers.Main){
            if (!responseManager.isReturn){
                responseManager.onLoading()
            }
        }
    }
    //进行网络请求
    requestManager.request(onSuccess = {
        //没拦截到异常时回调该函数体
        //若errcode = 0 ,则让Response走onSuccess() 并返回data与message
        if (this.isSuccess())responseManager.onSuccess(this.data.toString(), this.message)
        //否则的话 走onError() 并返回接口中的message
        else responseManager.onError(NetWorkException(this.errcode,this.message.toString()))
    }, onError = {
        //拦截到异常了就会回调该函数体
        this.printStackTrace()
        if (this is TimeoutCancellationException) {
            responseManager.onTimeOut()
        } else {
            responseManager.onError(NetWorkException.transitionException(this))
        }
    })
    //把请求的状态置为请求中，这样设置加载动画什么的不需要在掺在业务逻辑中了，而是在网络请求的响应中
}

fun MutableLiveData<Result>.response(
    lifecycleOwner: LifecycleOwner,
    loading: (() -> Unit)? = null,
    timeOut: (() -> Unit)? = null,
    error: ((exception: NetWorkException) -> Unit)? = null,
    success: ((data: String, m: String) -> Unit)? = null,
) {

    val observer = Observer<Result> {
        if (it != null) {
            it.apply {
                when (status) {
                    Result.Loading -> {
                        loading?.invoke()
                    }
                    Result.Success -> {
                        success?.invoke(response, it.message)
                    }
                    Result.Failure -> {
                        error?.invoke(exception)
                    }
                    Result.TimeOut -> {
                        timeOut?.invoke()
                    }
                }
            }
        } else {
            error?.invoke(NetWorkException(Error.RESULT_NULL,null))
        }
    }

    this.observe(lifecycleOwner, observer)
    val eventObserver = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            this.removeObserver(observer)
        }
    }
    lifecycleOwner.lifecycle.addObserver(eventObserver)
}

/**
 * 转换数据格式 把String转换成Response
 * 没办法 官方的转换器没办法定制转换
 */

suspend fun transition(request: suspend () -> String): Response {
    val dataString = request.invoke()
    return Response().apply {
        errcode = dataString.getJSONFields("errcode").toInt()
        message = dataString.getJSONFields("message")
        data = dataString.getJSONFields("data")
    }
}



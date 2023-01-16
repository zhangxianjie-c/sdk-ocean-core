package com.ocean.core.framework.network.dispose.extend

import androidx.lifecycle.MutableLiveData
import com.android.core.framework.network.dispose.pack.Result
import com.android.core.framework.network.exception.NetWorkException

/**
Created by Zebra-RD张先杰 on 2022年7月12日14:32:31

Description:响应后数据的处理
 */
class ResponseManager(private val data: MutableLiveData<Result>) : IResponse {
    @Volatile
    var isReturn = false
    override suspend fun onLoading() {
        postData(Result(Result.Loading))
    }

    override suspend fun onTimeOut() {
        postData(Result(Result.TimeOut))
        isReturn = true
    }

    override suspend fun onError(exception: NetWorkException) {
        postData(Result(Result.Failure,exception = exception))
        isReturn = true
    }

    override suspend fun onSuccess(data: String,message: String?) {
        postData(Result(Result.Success, response = data,message = message.toString()))
        isReturn = true
    }

    private fun postData(call: Result): Result {
        data.postValue(call)
        return call
    }
}
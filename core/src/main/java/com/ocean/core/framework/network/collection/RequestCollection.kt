package com.ocean.core.framework.network.collection

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.core.data.kInject.core.ext.single
import com.android.core.framework.network.SingleLiveEvent
import com.android.core.framework.network.dispose.pack.Result
import com.android.core.framework.network.dispose.request
import com.android.core.framework.network.dispose.transition
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


/**
 * Created by Zebra-RD张先杰 on 2022年6月30日14:08:21
 *
 * Description:顾名思义，是一个包含所有请求方法的集合，请求方法的具象化
 */
class RequestCollection(application: Application) : AndroidViewModel(application){
    private val apiService: ApiService by single()

    /**
     * 默认使用的liveData 当页面中只有一个请求时可以直接使用 多个请求尽量使用多个liveData 便于区分
     */
    val baseData : SingleLiveEvent<Result> = SingleLiveEvent()

    /**
     * Get请求
     * @param url 请求路径
     * @param path 路径
     */
    fun getData(url:String,path:String = "",bodyMap:HashMap<String,String> = hashMapOf(),liveData:MutableLiveData<Result>? = null) {
        //详细的处理逻辑转移到Process类
        request(liveData ?: baseData){
            transition{
                apiService.getData(url + path,bodyMap)
            }
        }
    }

    /**
     * Post请求
     * @param url 请求路径
     * @param bodyMap 请求体
     */
     fun postData(url:String,bodyMap:HashMap<String,String> = hashMapOf(),liveData:MutableLiveData<Result>? = null) {
       //详细的处理逻辑转移到Process类
         request(liveData ?: baseData){
             transition {
                 apiService.postData(url, bodyMap)
             }
         }
    }

    /**
     * Post请求  但是是JSON的形式
     * @param url 请求路径
     * @param bodyJson 请求JSON
     */

    fun postData(url:String, bodyJson: String, liveData:MutableLiveData<Result>? = null) {
        //详细的处理逻辑转移到Process类
        request(liveData ?: baseData){
            transition {
                apiService.postData(url, bodyJson.toRequestBody("application/json;charset=utf-8".toMediaType()))
            }
        }
    }

    fun postData(url:String, bodyMap: RequestBody, liveData:MutableLiveData<Result>? = null) {
        //详细的处理逻辑转移到Process类
        request(liveData ?: baseData){
            transition {
                apiService.postData(url, bodyMap)
            }
        }
    }
}
package com.ocean.core.framework.network

import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

/**
Created by Zebra-RD张先杰 on 2022年8月3日13:53:02

Description:JSON解析类，增强方法安全性健壮性 若要添加新类型 请在catch中增加新的空返回
 */
private const val TAG = "AliJSON"
class AliJSONException(text:String) : Exception(text)
fun <T>catch(run:(()->T)):T{
    var success:T? = null
    var error:Throwable? = null
    kotlin.runCatching {
        run()
    }.onFailure {
        it.printStackTrace()
        error = it
    }.onSuccess {
        success =  it
    }
    return if (success!=null) success!! else throw AliJSONException("在AliJson的解析中出现了一个预料外的错误：${error.toString()}")
}

fun String.initializeToObject(): JSONObject =
    catch<JSONObject> {
        JSON.parseObject(this)
    }



fun String.initializeToArray(): JSONArray =
    catch<JSONArray> {
        JSON.parseArray(this)
    }

/**
 * 字符串 直接取值
 */
fun String.getJSONFields(str: String, defaultSting: String = ""): String =
    catch<String> {
        initializeToObject().getJSONFields(str, defaultSting)
    }

/**
 * JSONObject 获取 String
 */
fun JSONObject.getJSONFields(str: String, defaultString: String = ""): String =
    catch<String> {
        getString(str) ?: defaultString
    }



/**
 * JSONArray 直接获取 子JSONObject 中的 String
 */
fun JSONArray.getJSONArrayItemFields(
    position: Int,
    str: String,
    defaultSting: String = "",
): String {
    return catch<String> {
         if (this.size > position) getSecureJSONObject(position).getJSONFields(str, defaultSting)
        else {
             Log.e(TAG, "getJSONArrayItemFields: java.lang.IndexOutOfBoundsException", )
            defaultSting
         }
    }
}

/**
 * 字符串直接 获取 不为空的JSONObject
 */
fun String.getSecureJSONObject(str: String): JSONObject =
    catch<JSONObject> {
        initializeToObject().getSecureJSONObject(str)
    }

/**
 * 字符串直接 获取 不为空的JSONObject
 */
fun JSONObject.getSecureJSONObject(str: String): JSONObject =
    catch<JSONObject> {
        getJSONObject(str) ?: JSONObject()
    }

/**
 * JSONArray 直接获取 子JSONObject 中的 String
 */
fun JSONArray.getSecureJSONObject(position: Int): JSONObject =
    catch<JSONObject> {
        if (position<this.size) getJSONObject(position) ?: JSONObject()
        else {
            Log.e(TAG, "getSecureJSONObject: java.lang.IndexOutOfBoundsException", )
            JSONObject()
        }
    }



/**
 * String 获取 安全的JSONArray
 */
fun String.getSecureJSONArray(str: String): JSONArray =
    catch<JSONArray> {
        initializeToObject().getSecureJSONArray(str)
    }


/**
 * JSONObject 获取 安全的JSONArray
 */
fun JSONObject.getSecureJSONArray(str: String): JSONArray {
    return catch<JSONArray> {
        val result = kotlin.runCatching {
            getJSONArray(str)
        }
         result.getOrNull() ?: JSONArray()
    }
}




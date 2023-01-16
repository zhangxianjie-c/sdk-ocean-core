package com.ocean.core.framework.network.control

import android.content.Context
import com.android.core.data.kInject.core.initScope
import com.android.core.framework.network.collection.ApiService
import okhttp3.Cache
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by Zebra-RD张先杰 on 2022年6月30日10:03:35
 *
 * Description:网络管理与配置
 */

/**
 * 创建Retrofit实例与ApiService实例
 */
class Network private constructor() {
    companion object {
        private val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { Network() }

        @Synchronized
        fun create() = instance
    }

    private lateinit var retrofit: Retrofit
    lateinit var context: Context
    fun config(onConfig: Config.() -> Unit) {
        val config = Config.create()
        config.context = context
        onConfig.invoke(config)
        config.build()
    }

    fun Config.build() {
        //创建OkHttpClient并配置
        val builder = OkHttpClient.Builder()
            .readTimeout(readTimeOut, TimeUnit.SECONDS)
            .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
            .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
            .cache(Cache(cacheFile, cacheSize))//设置缓存配置 缓存最大10M
        //设置CookieJar
        if (cookieJar!=null)
            builder.cookieJar(cookieJar!!)
        //配置拦截器
        interceptors.forEach {
            builder.addInterceptor(it)
        }
        val retrofitBuilder = Retrofit.Builder()
            .client(builder.build())
            .baseUrl(baseUrl)
        //设置格式转换器
        converters.forEach {
            retrofitBuilder.addConverterFactory(it)
        }
        retrofit = retrofitBuilder.build()
    }

    /**
     * 创建一个ApiService，并返回实例对象
     * @param tClass T::class.java
     */
    fun <T> createApi(tClass: Class<T>): T {
        return retrofit.create(tClass)
    }

    /**
     * 提前注入ApiService实例对象 使用时直接by single创建
     * @param tClass T::class.java
     */
    fun <T> lazyCreateApi(tClass: Class<T>) {
        initScope {
            single {
                createApi(ApiService::class.java)
            }
        }
    }

}

/**
 * Retrofit/Okhttp 相关配置
 */
class Config private constructor() {
    companion object {
        private val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { Config() }

        @Synchronized
        fun create() = instance
    }

    var baseUrl = ""
    lateinit var context: Context

    //缓存文件路径
    val cacheFile by lazy { File(context.externalCacheDir, "NetworkCache") }

    //缓存文件大小
    var cacheSize = 10 * 1024 * 1024L

    //设置连接的超时时间
    var connectTimeOut = 10L

    //设置读的超时时间
    var readTimeOut = 5L

    //设置写的超时时间
    var writeTimeOut = 5L

    //加入创建后的转换器，如:GsonConverterFactory.create(GsonBuilder().create())
    var converters = mutableListOf<Converter.Factory>()

    //Okhttp拦截器
    var interceptors = mutableListOf<Interceptor>()

    //Cookie持久化
     var cookieJar: CookieJar? = null
}
package com.ocean.core.framework.network.collection

import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by Zebra-RD张先杰 on 2022年6月30日11:28:40
 *
 * Description:这个是Retrofit的请求代理类
 */
interface ApiService {
    /**
     * HTTP, GET, POST, PUT, PATCH, DELETE, OPTIONS and HEAD
     * POST
     */
    @GET
    suspend fun getData(
        @Url url: String,@QueryMap body: HashMap<String, String>): String


    @FormUrlEncoded
    @POST
    suspend fun postData(
        @Url url: String,
        @FieldMap body: HashMap<String, String>,
    ): String

    @POST
    suspend fun postData(
        @Url url: String,
        @Body info: RequestBody
    ): String

}
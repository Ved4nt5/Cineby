package com.cineby.tv.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CinebyApiService {
    @GET("api/home")
    suspend fun getHome(): Response<ResponseBody>

    @GET("api/search")
    suspend fun search(@Query("q") query: String): Response<ResponseBody>

    @GET("api/content/{id}")
    suspend fun getContentDetails(@Path("id") id: String): Response<ResponseBody>
}

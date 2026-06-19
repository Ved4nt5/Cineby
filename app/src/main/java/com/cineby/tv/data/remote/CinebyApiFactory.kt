package com.cineby.tv.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CinebyApiFactory @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    fun create(baseUrl: String): CinebyApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl.ensureTrailingSlash())
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(CinebyApiService::class.java)
    }
}

private fun String.ensureTrailingSlash(): String = if (endsWith('/')) this else "$this/"

package com.bensadiku.dcf.api

import android.os.Build
import com.bensadiku.dcf.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit.Builder
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiFactory {

    private var apiServiceObj: ApiService? = null

    fun getApiService(): ApiService {
        if (apiServiceObj == null) {
            apiServiceObj = createApiService()
        }
        return apiServiceObj!!
    }

    private fun createApiService(): ApiService {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE

        val clientBuilder = OkHttpClient().newBuilder()
        clientBuilder.readTimeout(10, TimeUnit.SECONDS)
        clientBuilder.writeTimeout(10, TimeUnit.SECONDS)
        clientBuilder.connectTimeout(10, TimeUnit.SECONDS)
        clientBuilder.addInterceptor(interceptor)
        val client = clientBuilder.build()

        val restApiEndpoint: String = "https://catfact.ninja"
        val retrofit = Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(restApiEndpoint)
            .build()
        return retrofit.create(ApiService::class.java)
    }


}
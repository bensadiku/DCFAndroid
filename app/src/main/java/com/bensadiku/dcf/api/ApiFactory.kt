package com.bensadiku.dcf.api

import retrofit2.Retrofit.Builder
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    var apiServiceObj: ApiService? = null

    fun getApiService(): ApiService {
        if (apiServiceObj == null) {
            apiServiceObj = createApiService()
        }
        return apiServiceObj!!
    }

    private fun createApiService(): ApiService {
        val restApiEndpoint: String = "https://catfact.ninja"
        val retrofit = Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(restApiEndpoint)
            .build()
        return retrofit.create(ApiService::class.java)
    }


}
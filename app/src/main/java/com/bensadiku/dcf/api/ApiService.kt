package com.bensadiku.dcf.api

import com.bensadiku.dcf.models.CatFact
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {

    @GET("/fact")
    fun getCatFact(): Observable<CatFact>
}
package com.bensadiku.dcf.repository

import com.bensadiku.dcf.api.ApiFactory
import com.bensadiku.dcf.interfaces.CatFactCallback
import com.bensadiku.dcf.models.CatFact
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CatFactApiRepository @Inject constructor() {
    private var getCFSubscription: Disposable? = null

    private fun getCatFactsSingle(): Single<CatFact> {
        val getCatFactsObservable = ApiFactory.getApiService().getCatFact()

        return getCatFactsObservable
            .subscribeOn(Schedulers.io())
            .observeOn((AndroidSchedulers.mainThread()))
    }

    /**
     * Used by anyone who should handle the API callbacks
     */
    fun getCatFacts(catFactCallback: CatFactCallback) {
        getCFSubscription?.dispose()
        getCFSubscription = getCatFactsSingle()
            .subscribe(catFactCallback::gotFact, catFactCallback::failedFact)
    }

    /**
     * Used by the {@link PeriodicFact} worker
     */
    fun getCatFacts(): Single<CatFact> {
        return getCatFactsSingle()
    }

    fun clear() {
        getCFSubscription?.dispose()
        getCFSubscription = null
    }
}
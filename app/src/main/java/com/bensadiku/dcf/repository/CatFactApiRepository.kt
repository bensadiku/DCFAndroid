package com.bensadiku.dcf.repository

import com.bensadiku.dcf.api.ApiFactory
import com.bensadiku.dcf.interfaces.CatFactCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CatFactApiRepository {
    var disposable: Disposable? = null

    fun getCatFacts(catFactCallback: CatFactCallback) {
        val getCatFactsObservable = ApiFactory.getApiService().getCatFact()

        disposable = getCatFactsObservable
            .subscribeOn(Schedulers.io())
            .observeOn((AndroidSchedulers.mainThread()))
            .subscribe({
                catFactCallback.gotFact(it)
            }, {
                catFactCallback.failedFact(it)
            })
    }

}
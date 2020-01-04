package com.bensadiku.dcf.interfaces

import com.bensadiku.dcf.models.CatFact

interface CatFactCallback {

    fun gotFact(catFact: CatFact)

    fun failedFact(throwable: Throwable)
}
package com.bensadiku.dcf.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bensadiku.dcf.interfaces.CatFactCallback
import com.bensadiku.dcf.models.CatFact
import com.bensadiku.dcf.repository.CatFactApiRepository
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(private val catFactApiRepository: CatFactApiRepository) : ViewModel() {

    private val _hasResolvedSuccessfully = MutableLiveData<Boolean>(false)
    val hasResolvedSuccessfully: LiveData<Boolean>
        get() = _hasResolvedSuccessfully


    //checks if the connection is made successfully
    private val _catFact = MutableLiveData<String>()
    val catFact: LiveData<String>
        get() = _catFact

    fun getAndStartFactCounter() {
        catFactApiRepository.getCatFacts(object : CatFactCallback {
            override fun gotFact(catFact: CatFact) {
                Timber.d("got fact $catFact")
                _catFact.value = catFact.fact
                _hasResolvedSuccessfully.value = false
            }

            override fun failedFact(throwable: Throwable) {
                Timber.d("failedFact ${throwable.printStackTrace()}")
                _hasResolvedSuccessfully.value = true
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        catFactApiRepository.clear()
    }
}
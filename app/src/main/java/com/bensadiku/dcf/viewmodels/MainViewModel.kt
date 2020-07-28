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

    // Observed by the activity compose function for changes
    private val _catFact = MutableLiveData<String>()
    val catFact: LiveData<String>
        get() = _catFact

    fun getFact() {
        catFactApiRepository.getCatFacts(object : CatFactCallback {
            override fun gotFact(catFact: CatFact) {
                Timber.d("got fact $catFact")
                _catFact.value = catFact.fact
            }

            override fun failedFact(throwable: Throwable) {
                Timber.d("failedFact ${throwable.printStackTrace()}")
                _catFact.value = "Failed to fetch the latest fact: ${throwable.message}"
            }
        })
    }

    /**
     * @param fact is set by the notification
     */
    fun setFact(fact: String){
        _catFact.postValue(fact)
    }

    override fun onCleared() {
        super.onCleared()
        catFactApiRepository.clear()
    }
}
package com.bensadiku.dcf.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.bensadiku.dcf.repository.CatFactApiRepository
import com.bensadiku.dcf.util.PushNotification
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Single
import timber.log.Timber

class PeriodicFact @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val repository: CatFactApiRepository
) : RxWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "PeriodicFact"
    }

    override fun createWork(): Single<Result> {
        return repository.getCatFacts()
            .map { catFact ->
                Timber.d("got fact in worker $catFact")
                PushNotification.show(messageBody = catFact.fact, context = applicationContext)
                Result.success()
            }.onErrorReturn {
                Timber.w("PeriodicFact failed with the exception ${it.printStackTrace()}")
                Result.retry()
            }
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory
}

interface ChildWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): ListenableWorker
}

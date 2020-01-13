package com.bensadiku.dcf.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bensadiku.dcf.interfaces.CatFactCallback
import com.bensadiku.dcf.models.CatFact
import com.bensadiku.dcf.repository.CatFactApiRepository
import com.bensadiku.dcf.util.PushNotification
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import retrofit2.HttpException
import timber.log.Timber

class PeriodicFact @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val repository: CatFactApiRepository
) : Worker(appContext, params) {

    companion object {
        const val WORK_NAME = "PeriodicFact"
    }

    override fun doWork(): Result {

        return try {
            repository.getCatFacts(object : CatFactCallback {
                override fun gotFact(catFact: CatFact) {
                    Timber.d("got fact in worker $catFact")
                    PushNotification.show(messageBody = catFact.fact, context = applicationContext)
                    Result.success()
                }

                override fun failedFact(throwable: Throwable) {
                    Timber.d("failedFact in worker ${throwable.printStackTrace()}")
                    Result.failure()
                }
            })
            Result.success()
        } catch (exception: HttpException) {
            Timber.d("exception in worker ${exception.code()}")

            Result.retry()
        }
    }
    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory
}

interface ChildWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): ListenableWorker
}

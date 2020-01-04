package com.bensadiku.dcf.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bensadiku.dcf.R
import com.bensadiku.dcf.interfaces.CatFactCallback
import com.bensadiku.dcf.models.CatFact
import com.bensadiku.dcf.repository.CatFactApiRepository
import retrofit2.HttpException
import timber.log.Timber

class PeriodicFact(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {

    companion object {
        const val WORK_NAME = "PeriodicFact"
    }

    override fun doWork(): Result {

        val repository = CatFactApiRepository()
        return try {
            repository.getCatFacts(object : CatFactCallback {
                override fun gotFact(catFact: CatFact) {
                    Timber.d("got fact in worker $catFact")
                    displayNotification("New Cat fact", catFact.fact)
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

    private fun displayNotification(title: String, task: String) {
        val notificationManager: NotificationManager = getApplicationContext().getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CatFacts",
                "CatFacts",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        val notification = NotificationCompat.Builder(
            getApplicationContext(),
            "CatFactsChannel"
        )
            .setContentTitle(title)
            .setContentText(task)
            .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, notification.build());
    }
}


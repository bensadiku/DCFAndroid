package com.bensadiku.dcf

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.work.*
import com.bensadiku.dcf.workers.PeriodicFact
import com.crashlytics.android.Crashlytics
import timber.log.Timber
import java.util.concurrent.TimeUnit

open class CatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
        setupRecurringWork()
    }

    companion object {
        var instance: Application? = null
            private set
    }

    /** A tree which logs important information for crash reporting. */
    internal class CrashReportingTree : Timber.Tree() {

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            Crashlytics.log(priority, tag, message)

            if (t != null) {
                if (priority == Log.ERROR) {
                    Crashlytics.logException(t)
                } else if (priority == Log.WARN) {
                    Crashlytics.log(t.message)
                }
            }
        }
    }


    /**
     * Will fetch a fact every day and push a notification to the user.
     */
    private fun setupRecurringWork() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)//user wont be charged
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)//users are not using the device, so it wont affect usage
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<PeriodicFact>(
            24, TimeUnit.HOURS, 5,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this.applicationContext).enqueueUniquePeriodicWork(
            PeriodicFact.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}
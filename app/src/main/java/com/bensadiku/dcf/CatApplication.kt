package com.bensadiku.dcf

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.work.*
import com.bensadiku.dcf.di.AppComponent
import com.bensadiku.dcf.di.DaggerAppComponent
import com.bensadiku.dcf.util.Prefs
import com.bensadiku.dcf.workers.PeriodicFact
import com.crashlytics.android.Crashlytics
import timber.log.Timber
import java.util.concurrent.TimeUnit

open class CatApplication : Application(), Configuration.Provider  {

    private lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        instance = this
        //Initiate dagger
        appComponent = DaggerAppComponent.create()
        appComponent.inject(this)

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

    open fun getComponent(): AppComponent {
        return appComponent
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
     * Check if user disabled notifications, if so, disable all work manager instances
     * Else will fetch a fact every interval user set, by default 12 hrs, and push a notification to the user.
     */
    private fun setupRecurringWork() {
        if (!Prefs.getHasNotificationsEnabled()) {
            WorkManager.getInstance(this).cancelAllWork()
            return
        }
        val notificationTimer = Prefs.getNotificationTimeSeekbar()

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
            notificationTimer.interval.toLong(), notificationTimer.timeUnit, 5,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this.applicationContext).enqueueUniquePeriodicWork(
            PeriodicFact.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest
        )
    }

    /**
     * Get all the possible logs from the workers
     */
    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.VERBOSE)
            .build()
}
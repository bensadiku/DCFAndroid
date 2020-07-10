package com.bensadiku.dcf

import android.app.Application
import android.util.Log
import androidx.work.*
import com.bensadiku.dcf.di.AppComponent
import com.bensadiku.dcf.di.DaggerAppComponent
import com.bensadiku.dcf.di.factories.PeriodicWorkerFactory
import com.bensadiku.dcf.util.Prefs
import com.bensadiku.dcf.workers.PeriodicFact
import com.crashlytics.android.Crashlytics
import com.google.common.util.concurrent.ListenableFuture
import timber.log.Timber
import java.util.concurrent.ExecutionException
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

        setupWorkManager()
    }

    companion object {
        var instance: Application? = null
            private set
    }

    open fun getComponent(): AppComponent {
        return appComponent
    }

    /**
     * Sets up a custom worker factory for dependency injection
     */
    private fun setupWorkManager() {
        val factory: PeriodicWorkerFactory = appComponent.createWorkerFactory()
        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(factory).build())
        if (!isWorkScheduled(PeriodicFact.WORK_NAME)) {
            setupRecurringWork()
        }
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
            WorkManager.getInstance(this).cancelAllWorkByTag(PeriodicFact.WORK_NAME)
            return
        }
        val notificationTimer = Prefs.getNotificationTimeSeekbar()

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<PeriodicFact>(
                notificationTimer.interval.toLong(), notificationTimer.timeUnit, 15,
                TimeUnit.MINUTES
        ).setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .addTag(PeriodicFact.WORK_NAME)
                .build()
        WorkManager.getInstance(this.applicationContext).enqueueUniquePeriodicWork(
                PeriodicFact.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
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

    private fun isWorkScheduled(tag: String): Boolean {
        val instance = WorkManager.getInstance(this)
        val statuses: ListenableFuture<List<WorkInfo>> = instance.getWorkInfosByTag(tag)
        return try {
            var running = false
            val workInfoList: List<WorkInfo> = statuses.get()
            for (workInfo in workInfoList) {
                val state = workInfo.state
                running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }
}
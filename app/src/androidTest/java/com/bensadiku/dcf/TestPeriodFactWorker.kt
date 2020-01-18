package com.bensadiku.dcf

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.bensadiku.dcf.workers.PeriodicFact
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Created by Behxhet Sadiku on 20-01-18.
 */
@RunWith(AndroidJUnit4::class)
class TestPeriodFactWorker {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        val config = Configuration.Builder()
            // Set log level to Log.DEBUG to make it easier to debug
            .setMinimumLoggingLevel(Log.DEBUG)
            // Use a SynchronousExecutor here to make it easier to write tests
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    @Test
    @Throws(Exception::class)
    fun testPeriodicWork() {
        // Create request
        val request = PeriodicWorkRequestBuilder<PeriodicFact>(15, TimeUnit.MINUTES)
            .build()

        val workManager = WorkManager.getInstance(context)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        // Enqueue and wait for result.
        workManager.enqueue(request).result.get()
        // Tells the testing framework the period delay is met
        testDriver!!.setPeriodDelayMet(request.id)
        // Get WorkInfo and outputData
        val workInfo = workManager.getWorkInfoById(request.id).get()
        // Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))
    }
}
package com.bensadiku.dcf.di

import com.bensadiku.dcf.CatApplication
import com.bensadiku.dcf.activities.MainActivity
import com.bensadiku.dcf.activities.SettingsActivity
import com.bensadiku.dcf.di.factories.PeriodicWorkerFactory
import com.bensadiku.dcf.di.modules.ViewModelModule
import com.bensadiku.dcf.di.modules.WorkerAssistedInjectModule
import com.bensadiku.dcf.di.modules.WorkerBindingModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Behxhet Sadiku on 1/11/2020.
 */

@Singleton
@Component(
    modules = [
        AppModule::class, ViewModelModule::class,
        WorkerAssistedInjectModule::class, WorkerBindingModule::class
    ]
)
interface AppComponent {

    fun inject(application: CatApplication)
    fun inject(activity: MainActivity)
    fun inject(activity: SettingsActivity)
    fun createWorkerFactory(): PeriodicWorkerFactory
}
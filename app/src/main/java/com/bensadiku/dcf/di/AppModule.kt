package com.bensadiku.dcf.di

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Behxhet Sadiku on 1/11/2020.
 */

@Module
class AppModule
    (private val app: Application) {
    @Provides
    @Singleton
    fun application(): Application {
        return app
    }
}
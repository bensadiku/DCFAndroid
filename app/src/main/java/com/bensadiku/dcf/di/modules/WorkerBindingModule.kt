package com.bensadiku.dcf.di.modules

import com.bensadiku.dcf.di.keys.WorkerKey
import com.bensadiku.dcf.workers.ChildWorkerFactory
import com.bensadiku.dcf.workers.PeriodicFact
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Behxhet Sadiku on 1/13/2020.
 */
@Module
interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(PeriodicFact::class)
    fun bindPeriodicFactWorker(factory: PeriodicFact.Factory): ChildWorkerFactory
}


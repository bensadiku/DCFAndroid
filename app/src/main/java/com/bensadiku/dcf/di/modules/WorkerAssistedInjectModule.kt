package com.bensadiku.dcf.di.modules

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

/**
 * Created by Behxhet Sadiku on 1/13/2020.
 */
@Module(includes = [AssistedInject_WorkerAssistedInjectModule::class])
@AssistedModule
interface WorkerAssistedInjectModule
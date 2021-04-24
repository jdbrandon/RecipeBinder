package com.jeffbrandon.recipebinder.dagger

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class CoroutineModule {

    @Provides
    fun providesJob(): Job = SupervisorJob()

    @Provides
    fun providesCoroutineContext(job: Job): CoroutineContext = job + Dispatchers.Main

    @Provides
    fun providesCoroutineScope(job: Job, context: CoroutineContext): CoroutineScope =
        object : CoroutineScope {
            override val coroutineContext: CoroutineContext
                get() = job + context
        }
}

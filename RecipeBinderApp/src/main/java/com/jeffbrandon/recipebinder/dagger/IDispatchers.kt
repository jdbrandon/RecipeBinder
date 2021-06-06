package com.jeffbrandon.recipebinder.dagger

import kotlinx.coroutines.CoroutineDispatcher

interface IDispatchers {
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
}

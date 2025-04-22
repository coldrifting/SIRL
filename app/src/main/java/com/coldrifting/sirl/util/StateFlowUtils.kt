package com.coldrifting.sirl.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn


fun <T> Flow<T>.toStateFlow(scope: CoroutineScope, defaultVal: T): StateFlow<T> {
    return this.stateIn(
        scope = scope,
        started = SharingStarted.Companion.Eagerly,
        initialValue = defaultVal
    )
}

fun <T> Flow<List<T>>.toStateFlow(scope: CoroutineScope): StateFlow<List<T>> {
    return this.toStateFlow(scope, listOf())
}
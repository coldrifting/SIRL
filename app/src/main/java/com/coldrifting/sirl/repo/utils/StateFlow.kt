package com.coldrifting.sirl.repo.utils

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <T> Flow<T>.toStateFlow(scope: CoroutineScope, initialValue: T): StateFlow<T> =
    this.stateIn(scope, SharingStarted.Eagerly, initialValue)

fun <T> Flow<T>.toNullableStateFlow(scope: CoroutineScope, initialValue: T?): StateFlow<T?> =
    this.stateIn(scope, SharingStarted.Eagerly, initialValue)

fun <T> Flow<List<T>>.toStateFlow(scope: CoroutineScope): StateFlow<List<T>> =
    this.toStateFlow(scope, listOf())

fun <T : Any> Query<T>.toStateFlow(scope: CoroutineScope, initialValue: T): StateFlow<T> =
    this.asFlow().mapToOne(scope.coroutineContext).toStateFlow(scope, initialValue)

fun <T : Any> Query<T>.toNullableStateFlow(scope: CoroutineScope, initialValue: T?): StateFlow<T?> =
    this.asFlow().mapToOneOrNull(scope.coroutineContext).toNullableStateFlow(scope, initialValue)

fun <T : Any, R> Query<T>.toStateFlow(
    scope: CoroutineScope,
    initialValue: R,
    transform: (T) -> R
): StateFlow<R> =
    this.asFlow().mapToOne(scope.coroutineContext).map(transform).toStateFlow(scope, initialValue)

fun <T : Any, R> Query<T>.toNullableStateFlow(
    scope: CoroutineScope,
    initialValue: R?,
    transform: (T?) -> R?
): StateFlow<R?> =
    this.asFlow().mapToOneOrNull(scope.coroutineContext).map(transform)
        .toNullableStateFlow(scope, initialValue)

fun <T : Any> Query<T>.toListStateFlow(scope: CoroutineScope): StateFlow<List<T>> =
    this.asFlow().mapToList(scope.coroutineContext).toStateFlow(scope)

fun <T : Any, R> Query<T>.toListStateFlow(
    scope: CoroutineScope,
    transform: (List<T>) -> List<R>
): StateFlow<List<R>> =
    this.asFlow().mapToList(scope.coroutineContext).map(transform).toStateFlow(scope)

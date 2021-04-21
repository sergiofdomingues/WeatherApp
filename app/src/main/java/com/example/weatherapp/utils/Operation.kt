package com.example.weatherapp.utils

import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onErrorReturn

sealed class Operation<out T> {
    data class Success<T>(val result: T) : Operation<T>()
    data class Error<T>(val throwable: Throwable) : Operation<T>()
}

val <T> Operation<T>.isSuccessful get() = this is Operation.Success<T>
val <T> Operation<T>.isError get() = this is Operation.Error<T>

fun <T> Single<T>.toOperation(): Single<Operation<T>> =
    map { Operation.Success(it) as Operation<T> }
        .onErrorReturn { Operation.Error(it) }

fun <T> T.wrapToOperation() = try {
    Operation.Success(this)
} catch (throwable: Throwable) {
    Operation.Error(throwable)
}
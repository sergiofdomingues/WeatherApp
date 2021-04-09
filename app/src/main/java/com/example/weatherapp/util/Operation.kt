package com.example.weatherapp.util

import io.reactivex.Single

sealed class Operation<out T> {
    data class Success<T>(val result: T) : Operation<T>()
    data class Error<T>(val throwable: Throwable) : Operation<T>()
}

fun <T> Single<T>.toOperation(): Single<Operation<T>> =
    map { Operation.Success(it) as Operation<T> }
        .onErrorReturn { Operation.Error(it) }

fun <T> T.wrapToOperation() = try {
    Operation.Success(this)
} catch (throwable: Throwable) {
    Operation.Error(throwable)
}
package com.yavuzmobile.borsaanalizim.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.zip

fun <T1, T2, T3, R> zip(
    first: Flow<T1>,
    second: Flow<T2>,
    third: Flow<T3>,
    transform: suspend (T1, T2, T3) -> R
): Flow<R> =
    first.zip(second) { a, b -> a to b }
        .zip(third) { (a, b), c ->
            transform(a, b, c)
        }

fun <T1, T2, T3, T4, R> zip(
    first: Flow<T1>,
    second: Flow<T2>,
    third: Flow<T3>,
    four: Flow<T4>,
    transform: suspend (T1, T2, T3, T4) -> R
): Flow<R> =
    first.zip(second) { a, b -> a to b }
        .zip(third) { prevFlow, threeFlow ->
            prevFlow to threeFlow
        }
        .zip(four) { (a, b), c->
            transform(a.first, a.second, b, c)
        }
package com.example.core.network

import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 包名称： com.example.core.network
 * 类名称：RetrofitEnqueue
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-16 15:34
 *
 */
suspend fun <T> Call<T>.await(): T = suspendCancellableCoroutine { continuation ->
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                @Suppress("UNCHECKED_CAST")
                continuation.resume(response.body() as T)
            } else {
                continuation.resumeWithException(
                    HttpException(response)
                )
            }
        }
        override fun onFailure(call: Call<T>, t: Throwable) {
            if (continuation.isCancelled) return
            continuation.resumeWithException(t)
        }
    })
    continuation.invokeOnCancellation {
        try {
            cancel()
        } catch (_: Exception) {
        }
    }
}
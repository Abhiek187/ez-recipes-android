package com.abhiek.ezrecipes.data.interceptors

import android.content.Context
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * An interceptor to cache network responses
 *
 * @param context The application context
 * @param sizeInMB The max size of the cache in MB
 * @param age The max time to store the cache
 * @param units The units of the age
 */
class CacheInterceptor(
    private val context: Context,
    private val sizeInMB: Int,
    private val age: Int,
    private val units: TimeUnit
): Interceptor {
    private val sizeInBytes = sizeInMB * 1024 * 1024L
    // Cache directory: /data/data/PACKAGE-NAME/cache (/data/user/0 == /data/data)
    val cache = Cache(context.cacheDir, sizeInBytes)

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val cacheControl = CacheControl.Builder()
            .maxAge(age, units)
            .build()

        return response.newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .header("Cache-Control", cacheControl.toString())
            .build()
    }
}

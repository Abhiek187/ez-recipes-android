package com.abhiek.ezrecipes.data.interceptors

import android.content.Context
import android.os.Build
import com.abhiek.ezrecipes.BuildConfig
import com.abhiek.ezrecipes.R
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Constructs a User-Agent header with information about the app and device
 */
class UserAgentInterceptor(context: Context): Interceptor {
    private val userAgent: String

    init {
        val packageName = context.packageName
        val appName = context.getString(R.string.app_name)
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        val androidVersion = "Android ${Build.VERSION.RELEASE}"
        val okhttpVersion = BuildConfig.OK_HTTP_VERSION

        userAgent = "$appName/$versionName ($packageName; build:$versionCode; $androidVersion) " +
                "okhttp/$okhttpVersion"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(request)
    }
}

package com.abhiek.ezrecipes.data.interceptors

import android.util.Log
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import java.util.concurrent.TimeUnit

/**
 * A variant of HttpLoggingInterceptor that masks sensitive data in the request headers and body
 *
 * Based on https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/src/main/kotlin/okhttp3/logging/HttpLoggingInterceptor.kt
 */
class SecureHttpLoggingInterceptor: Interceptor {
    companion object {
        private const val TAG = "SensitiveHttpLoggingInterceptor"
        private const val MASK = "██"
    }

    // Header checks should be case insensitive
    private val headersToRedact = listOf("authorization", "cookie")
    private val fieldsToRedact = listOf("password")

    override fun intercept(chain: Interceptor.Chain): Response {
        // Top-level request
        val request = chain.request()
        val requestBody = request.body
        val connection = chain.connection()

        val requestStartMessage = StringBuilder().apply {
            append("--> ${request.method} ${request.url}")
            if (connection != null) {
                append(" ${connection.protocol()}")
            }
            if (requestBody != null) {
                append(" (${requestBody.contentType()})")
            }
        }.toString()

        Log.i(TAG, requestStartMessage)

        // Request headers
        val headers = request.headers

        if (requestBody != null) {
            requestBody.contentType()?.let {
                if (headers["Content-Type"] == null) {
                    Log.i(TAG, "Content-Type: $it")
                }
            }
            if (requestBody.contentLength() != -1L) {
                if (headers["Content-Length"] == null) {
                    Log.i(TAG, "Content-Length: ${requestBody.contentLength()}")
                }
            }
        }

        logHeaders(headers)

        // Request body
        if (requestBody == null) {
            Log.i(TAG, "--> END ${request.method}")
        } else if (bodyHasUnknownEncoding(request.headers)) {
            Log.i(TAG, "--> END ${request.method} (encoded body omitted)")
        } else if (requestBody.isDuplex()) {
            Log.i(TAG, "--> END ${request.method} (duplex request body omitted)")
        } else if (requestBody.isOneShot()) {
            Log.i(TAG, "--> END ${request.method} (one-shot body omitted)")
        } else {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val requestBodyString = buffer.readUtf8()
            val redactedRequestBodyString = redactBody(requestBodyString)

            Log.i(TAG, "")
            Log.i(TAG, redactedRequestBodyString)
            Log.i(TAG, "--> END ${request.method} (${requestBody.contentLength()}-byte body)")
        }

        // Top-level response
        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (error: Exception) {
            Log.e(TAG, "<-- HTTP FAILED: $error")
            throw error
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()

        val responseStartMessage = StringBuilder().apply {
            append("<-- ${response.code}")
            if (response.message.isNotEmpty()) {
                append(" ${response.message}")
            }
            append(" ${response.request.url} (${tookMs}ms)")
        }.toString()
        Log.i(TAG, responseStartMessage)

        // Response headers
        logHeaders(response.headers)

        // Response body
        if (!response.promisesBody()) {
            Log.i(TAG, "<-- END HTTP")
        } else if (bodyHasUnknownEncoding(response.headers)) {
            Log.i(TAG, "<-- END HTTP (encoded body omitted)")
        } else {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // buffer the entire body
            val buffer = source.buffer

            if (contentLength != 0L) {
                Log.i(TAG, "")
                Log.i(TAG, buffer.clone().readUtf8())
            }

            Log.i(TAG, "<-- END HTTP (${buffer.size}-byte body)")
        }

        return response
    }

    private fun logHeaders(headers: Headers) {
        for ((name, rawValue) in headers) {
            val value = if (name.lowercase() in headersToRedact) MASK else rawValue
            Log.i(TAG, "$name: $value")
        }
    }

    private fun redactBody(requestBodyString: String): String {
        var redactedBodyString = requestBodyString

        for (field in fieldsToRedact) {
            redactedBodyString = redactedBodyString.replace(
                "$field\":\"[^\"]+\"".toRegex(),
                "$field\":\"$MASK\""
            )
        }

        return redactedBodyString
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }
}

package com.abhiek.ezrecipes.data.chef

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
    }

    private val headersToReact = listOf("Authorization", "Cookie")

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

        Log.d(TAG, requestStartMessage)

        // Request headers
        val headers = request.headers

        if (requestBody != null) {
            requestBody.contentType()?.let {
                if (headers["Content-Type"] == null) {
                    Log.d(TAG, "Content-Type: $it")
                }
            }
            if (requestBody.contentLength() != -1L) {
                if (headers["Content-Length"] == null) {
                    Log.d(TAG, "Content-Length: ${requestBody.contentLength()}")
                }
            }
        }

        logHeaders(headers)

        // Request body
        if (requestBody == null) {
            Log.d(TAG, "--> END ${request.method}")
        } else if (bodyHasUnknownEncoding(request.headers)) {
            Log.d(TAG, "--> END ${request.method} (encoded body omitted)")
        } else if (requestBody.isDuplex()) {
            Log.d(TAG, "--> END ${request.method} (duplex request body omitted)")
        } else if (requestBody.isOneShot()) {
            Log.d(TAG, "--> END ${request.method} (one-shot body omitted)")
        } else {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val requestBodyString = buffer.readUtf8()
            val redactedRequestBodyString = redactSensitiveData(requestBodyString)

            Log.d(TAG, "")
            Log.d(TAG, redactedRequestBodyString)
            Log.d(TAG, "--> END ${request.method} (${requestBody.contentLength()}-byte body)")
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
        Log.d(TAG, responseStartMessage)

        // Response headers
        logHeaders(response.headers)

        // Response body
        if (!response.promisesBody()) {
            Log.d(TAG, "<-- END HTTP")
        } else if (bodyHasUnknownEncoding(response.headers)) {
            Log.d(TAG, "<-- END HTTP (encoded body omitted)")
        } else {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // buffer the entire body
            val buffer = source.buffer

            if (contentLength != 0L) {
                Log.d(TAG, "")
                Log.d(TAG, buffer.clone().readUtf8())
            }

            Log.d(TAG, "<-- END HTTP (${buffer.size}-byte body)")
        }

        return response
    }

    private fun logHeaders(headers: Headers) {
        for ((name, rawValue) in headers) {
            val value = if (name in headersToReact) "██" else rawValue
            Log.d(TAG, "$name: $value")
        }
    }

    private fun redactSensitiveData(requestBodyString: String): String {
        // Implement your logic to redact sensitive data
        // For example, you can use regex to find and replace password fields
        // with "***"
        return requestBodyString.replace(
            "password\":\"[^\"]+\"".toRegex(),
            "password\":\"***\""
        )
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }
}

package com.abhiek.ezrecipes.data.chef

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

class SensitiveHttpLoggingInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body

        val requestStartMessage = StringBuilder().apply {
            append("--> ${request.method} ${request.url}")
            if (requestBody != null) {
                append(" (${requestBody.contentType()})")
            }
            append("\n")
        }.toString()

        println(requestStartMessage)

        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val requestBodyString = buffer.readUtf8()

            // Redact sensitive data from the request body string
            val redactedRequestBodyString = redactSensitiveData(requestBodyString)

            println("Request body: $redactedRequestBodyString")
        }

        val response = chain.proceed(request)

        val responseBody = response.body
        val responseStartMessage =
            "<-- ${response.code} ${response.message} ${response.request.url}\n"

        println(responseStartMessage)

        if (responseBody != null) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer
            val responseBodyString = buffer.clone().readString(Charsets.UTF_8)

            println("Response body: $responseBodyString")
        }

        return response
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
}

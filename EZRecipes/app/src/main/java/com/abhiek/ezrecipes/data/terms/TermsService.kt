package com.abhiek.ezrecipes.data.terms

import android.content.Context
import com.abhiek.ezrecipes.data.interceptors.UserAgentInterceptor
import com.abhiek.ezrecipes.data.models.Term
import com.abhiek.ezrecipes.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface TermsService {
    @GET(".")
    suspend fun getTerms(): Response<List<Term>>

    companion object {
        private lateinit var termsService: TermsService

        // Initialize the Retrofit service when first referencing the singleton
        fun getInstance(context: Context): TermsService {
            if (Companion::termsService.isInitialized) return termsService

            // Log request and response lines and their respective headers and bodies
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(
                HttpLoggingInterceptor.Level.BODY
            )
            loggingInterceptor.redactHeader("Authorization")
            loggingInterceptor.redactHeader("Cookie")
            val userAgentInterceptor = UserAgentInterceptor(context)

            val httpClient = OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(userAgentInterceptor)
                .readTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()

            // Convert responses to GSON (Google JSON)
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL + Constants.TERMS_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()

            termsService = retrofit.create(TermsService::class.java)
            return termsService
        }
    }
}

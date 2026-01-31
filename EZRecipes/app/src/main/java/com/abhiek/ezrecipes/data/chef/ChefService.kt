package com.abhiek.ezrecipes.data.chef

import android.content.Context
import com.abhiek.ezrecipes.data.interceptors.CacheInterceptor
import com.abhiek.ezrecipes.data.interceptors.SecureHttpLoggingInterceptor
import com.abhiek.ezrecipes.data.interceptors.UserAgentInterceptor
import com.abhiek.ezrecipes.data.models.*
import com.abhiek.ezrecipes.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ChefService {
    @GET(".")
    suspend fun getChef(
        @Header("Authorization") token: String
    ): Response<Chef>

    @POST(".")
    suspend fun createChef(
        @Body credentials: LoginCredentials
    ): Response<LoginResponse>

    @PATCH(".")
    suspend fun updateChef(
        @Body fields: ChefUpdate,
        @Header("Authorization") token: String? = null
    ): Response<ChefEmailResponse>

    @DELETE(".")
    suspend fun deleteChef(
        @Header("Authorization") token: String
    ): Response<Void> // empty response

    @POST("verify")
    suspend fun verifyEmail(
        @Header("Authorization") token: String
    ): Response<ChefEmailResponse>

    @POST("login")
    suspend fun login(
        @Body credentials: LoginCredentials
    ): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Void>

    @GET("oauth")
    suspend fun getAuthUrls(
        @Query("redirectUrl") redirectUrl: String
    ): Response<List<AuthUrl>>

    @POST("oauth")
    suspend fun loginWithOAuth(
        @Body oAuthRequest: OAuthRequest,
        @Header("Authorization") token: String? = null
    ): Response<LoginResponse>

    @DELETE("oauth")
    suspend fun unlinkOAuthProvider(
        @Query("providerId") providerId: Provider,
        @Header("Authorization") token: String
    ): Response<Token>

    @GET("passkey/create")
    suspend fun getNewPasskeyChallenge(
        @Header("Authorization") token: String
    ): Response<PasskeyCreationOptions>

    @GET("passkey/auth")
    suspend fun getExistingPasskeyChallenge(
        @Query("email") email: String
    ): Response<PasskeyRequestOptions>

    @POST("passkey/verify")
    suspend fun validatePasskey(
        @Body passkeyResponse: PasskeyClientResponse,
        @Query("email") email: String? = null,
        @Header("Authorization") token: String? = null
    ): Response<Token>

    @DELETE("passkey")
    suspend fun deletePasskey(
        @Query("id") id: String,
        @Header("Authorization") token: String
    ): Response<Token>

    companion object {
        private lateinit var chefService: ChefService

        fun getInstance(context: Context): ChefService {
            if (Companion::chefService.isInitialized) return chefService

            val loggingInterceptor = SecureHttpLoggingInterceptor()
            val cacheInterceptor = CacheInterceptor(
                context = context,
                sizeInMB = 1,
                age = 5,
                units = TimeUnit.MINUTES
            )
            val userAgentInterceptor = UserAgentInterceptor(context)

            val httpClient = OkHttpClient().newBuilder()
                .cache(cacheInterceptor.cache)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(cacheInterceptor)
                .addInterceptor(userAgentInterceptor)
                .readTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL + Constants.CHEFS_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()

            chefService = retrofit.create(ChefService::class.java)
            return chefService
        }
    }
}

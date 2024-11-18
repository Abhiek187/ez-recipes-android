package com.abhiek.ezrecipes.data.chef

import com.abhiek.ezrecipes.data.models.*
import com.abhiek.ezrecipes.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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

    companion object {
        private lateinit var chefService: ChefService

        val instance: ChefService
            get() {
                if (Companion::chefService.isInitialized) return chefService

                val loggingInterceptor = HttpLoggingInterceptor().setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
//                val loggingInterceptor = SensitiveHttpLoggingInterceptor()
                val httpClient = OkHttpClient().newBuilder()
                    .addInterceptor(loggingInterceptor)
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

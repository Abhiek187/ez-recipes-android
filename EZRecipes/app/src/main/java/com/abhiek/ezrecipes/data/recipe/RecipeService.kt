package com.abhiek.ezrecipes.data.recipe

import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.models.RecipeFilter
import com.abhiek.ezrecipes.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit

// The DataSource for the recipes API
interface RecipeService {
    @GET("")
    suspend fun getRecipesByFilter(
        @QueryMap filter: RecipeFilter
    ): Response<List<Recipe>>

    @GET("random")
    suspend fun getRandomRecipe(): Response<Recipe>

    @GET("{id}")
    suspend fun getRecipeById(
        @Path("id") id: Int
    ): Response<Recipe>

    companion object {
        private lateinit var recipeService: RecipeService

        // Initialize the Retrofit service when first referencing the singleton
        val instance: RecipeService
            get() {
                if (Companion::recipeService.isInitialized) return recipeService

                // Log request and response lines and their respective headers and bodies
                val loggingInterceptor = HttpLoggingInterceptor().setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
                // Extend the default timeout of 10 seconds to account for cold starts
                val httpClient = OkHttpClient().newBuilder()
                    .addInterceptor(loggingInterceptor)
                    .readTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .connectTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .build()

                // Convert responses to GSON (Google JSON)
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.SERVER_BASE_URL + Constants.RECIPE_PATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build()

                recipeService = retrofit.create(RecipeService::class.java)
                return recipeService
            }
    }
}

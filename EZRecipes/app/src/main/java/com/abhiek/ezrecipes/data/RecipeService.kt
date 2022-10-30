package com.abhiek.ezrecipes.data

import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// The DataSource for the recipes API
interface RecipeService {
    @GET(Constants.RANDOM_RECIPE_PATH)
    suspend fun getRandomRecipe(): Response<Recipe>

    @GET("{id}")
    suspend fun getRecipeById(
        @Path("id") id: String
    ): Response<Recipe>

    companion object {
        private lateinit var recipeService: RecipeService

        // Initialize the Retrofit service when first referencing the singleton
        val instance: RecipeService
            get() {
                if (::recipeService.isInitialized) return recipeService

                // Log request and response lines and their respective headers and bodies
                val loggingInterceptor = HttpLoggingInterceptor().setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
                val httpClient = OkHttpClient().newBuilder()
                    .addInterceptor(loggingInterceptor)
                    .build()

                // Convert responses to GSON (Google JSON)
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.RECIPE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build()

                recipeService = retrofit.create(RecipeService::class.java)
                return recipeService
            }
    }
}

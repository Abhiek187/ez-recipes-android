package com.abhiek.ezrecipes.data.recipe

import com.abhiek.ezrecipes.data.adapters.CuisineTypeAdapter
import com.abhiek.ezrecipes.data.adapters.MealTypeAdapter
import com.abhiek.ezrecipes.data.adapters.SpiceLevelTypeAdapter
import com.abhiek.ezrecipes.data.models.*
import com.abhiek.ezrecipes.utils.Constants
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.QueryName
import java.util.concurrent.TimeUnit

// The DataSource for the recipes API
interface RecipeService {
    @GET(".") // same as the base URL
    @JvmSuppressWildcards // allow Any map value
    suspend fun getRecipesByFilter(
        // Just key, no value
        @QueryName vegetarian: String? = null,
        @QueryName vegan: String? = null,
        @QueryName glutenFree: String? = null,
        @QueryName healthy: String? = null,
        @QueryName cheap: String? = null,
        @QueryName sustainable: String? = null,
        // Multiple repeating query parameters
        @Query("spice-level") spiceLevels: List<SpiceLevel> = listOf(),
        @Query("type") mealTypes: List<MealType> = listOf(),
        @Query("culture") cuisines: List<Cuisine> = listOf(),
        // All other filters that can be serialized like normal
        @QueryMap filters: Map<String, Any> = mapOf()
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
                val gson = GsonBuilder()
                    .registerTypeAdapter(Cuisine::class.java, CuisineTypeAdapter())
                    .registerTypeAdapter(MealType::class.java, MealTypeAdapter())
                    .registerTypeAdapter(SpiceLevel::class.java, SpiceLevelTypeAdapter())
                    .create()
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.SERVER_BASE_URL + Constants.RECIPE_PATH)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build()

                recipeService = retrofit.create(RecipeService::class.java)
                return recipeService
            }
    }
}

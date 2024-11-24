package com.abhiek.ezrecipes.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.AppDatabase
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.data.storage.RecentRecipeDao
import com.abhiek.ezrecipes.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.testing.FakeReviewManager
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
@ExtendWith(MockKExtension::class)
internal class MainViewModelTest {
    private lateinit var mockService: MockRecipeService
    private lateinit var recentRecipeDao: RecentRecipeDao
    private lateinit var mockDataStoreService: DataStoreService
    private lateinit var mockReviewManager: FakeReviewManager
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var context: Context
    @MockK
    private lateinit var activity: Activity

    private fun mockLog() {
        mockkStatic(Log::class)
        every { Log.d(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
    }

    @BeforeEach
    fun setUp() {
        mockService = MockRecipeService
        recentRecipeDao = AppDatabase.getInstance(context, inMemory = true).recentRecipeDao()
        mockDataStoreService = mockkClass(DataStoreService::class) {
            coJustRun { incrementRecipesViewed() }
        }
        mockReviewManager = spyk(FakeReviewManager(context))

        viewModel = MainViewModel(
            recipeRepository = RecipeRepository(mockService, recentRecipeDao),
            dataStoreService = mockDataStoreService,
            reviewManager = mockReviewManager
        )

        mockLog()
    }

    @Test
    fun getRandomRecipeSuccess() = runTest {
        // Given an instance of MainViewModel
        // When the getRandomRecipe() method is called
        mockService.isSuccess = true
        val fromHome = true
        viewModel.getRandomRecipe(fromHome)

        // Then the recipe property should match the mock recipe
        assertEquals(viewModel.recipe, mockService.recipes[1])
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.isLoading)
        assertEquals(viewModel.isRecipeLoaded, fromHome)
        assertFalse(viewModel.showRecipeAlert)
    }

    @Test
    fun getRandomRecipeError() = runTest {
        // Given an instance of MainViewModel
        // When the getRandomRecipe() method is called with isSuccess = false
        mockService.isSuccess = false
        val fromHome = true
        viewModel.getRandomRecipe(fromHome)

        // Then the recipeError property should match the mock recipeError
        assertNull(viewModel.recipe)
        assertEquals(viewModel.recipeError, mockService.recipeError)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isRecipeLoaded)
    }

    @Test
    fun getRecipeByIdSuccess() = runTest {
        // Given an instance of MainViewModel
        // When the getRecipeById() method is called
        mockService.isSuccess = true
        viewModel.getRecipeById(1)

        // Then the recipe property should match the mock recipe
        assertEquals(viewModel.recipe, mockService.recipes[1])
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isRecipeLoaded) // fromHome is false by default
        assertFalse(viewModel.showRecipeAlert)
    }

    @Test
    fun getRecipeByIdError() = runTest {
        // Given an instance of MainViewModel
        // When the getRecipeById() method is called with isSuccess = false
        mockService.isSuccess = false
        viewModel.getRecipeById(1)

        // Then the recipeError property should match the mock recipeError
        assertNull(viewModel.recipe)
        assertEquals(viewModel.recipeError, mockService.recipeError)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isRecipeLoaded)
    }

    @Test
    fun incrementRecipesViewed() = runTest {
        // Given an instance of MainViewModel
        // When incrementRecipesViewed() is called
        viewModel.incrementRecipesViewed()

        // Then the corresponding DataStore method should be called
        coVerify { mockDataStoreService.incrementRecipesViewed() }
    }

    @Test
    fun `don't present a review on first launch`() = runTest {
        // Given a user that's launched the app
        // When presentReviewIfQualified() is called
        viewModel.presentReviewIfQualified(activity)

        // Then it should return immediately
        coVerify(exactly = 0) { mockDataStoreService.getRecipesViewed() }
        coVerify(exactly = 0) { mockDataStoreService.getLastVersionReviewed() }
    }

    @Test
    fun `don't present a review if not enough recipes are viewed`() = runTest {
        // Given a user that's viewed less than the required number of recipes
        coEvery { mockDataStoreService.getRecipesViewed() } returns 1
        coEvery {
            mockDataStoreService.getLastVersionReviewed()
        } returns MainViewModel.CURRENT_VERSION - 1

        // When presentReviewIfQualified() is called (twice)
        viewModel.presentReviewIfQualified(activity)
        viewModel.presentReviewIfQualified(activity)

        // Then it shouldn't request a review
        coVerify { mockDataStoreService.getRecipesViewed() }
        coVerify { mockDataStoreService.getLastVersionReviewed() }
        verify(exactly = 0) { mockReviewManager.requestReviewFlow() }
    }

    @Test
    fun `don't present a review on the same version`() = runTest {
        // Given a user that was already presented a review on the current app version
        coEvery {
            mockDataStoreService.getRecipesViewed()
        } returns Constants.RECIPES_TO_PRESENT_REVIEW
        coEvery {
            mockDataStoreService.getLastVersionReviewed()
        } returns MainViewModel.CURRENT_VERSION

        // When presentReviewIfQualified() is called
        viewModel.presentReviewIfQualified(activity)
        viewModel.presentReviewIfQualified(activity)

        // Then it shouldn't request a review
        coVerify { mockDataStoreService.getRecipesViewed() }
        coVerify { mockDataStoreService.getLastVersionReviewed() }
        verify(exactly = 0) { mockReviewManager.requestReviewFlow() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `present a review if qualified`() = runTest {
        // Given a user that's viewed enough recipes and hasn't reviewed on the current app version
        coEvery {
            mockDataStoreService.getRecipesViewed()
        } returns Constants.RECIPES_TO_PRESENT_REVIEW
        coEvery {
            mockDataStoreService.getLastVersionReviewed()
        } returns MainViewModel.CURRENT_VERSION - 1
        coJustRun {
            mockDataStoreService.setLastVersionReviewed(any<Int>())
        }

        // Mock all the completion listeners
        val requestFlowTask = mockk<Task<ReviewInfo>>()
        every { mockReviewManager.requestReviewFlow() } returns requestFlowTask
        val reviewInfoMock = mockk<ReviewInfo>()
        every { requestFlowTask.isSuccessful } returns true
        every { requestFlowTask.result } returns reviewInfoMock
        val requestListenerSlot = slot<OnCompleteListener<ReviewInfo>>()
        every { requestFlowTask.addOnCompleteListener(capture(requestListenerSlot)) } answers {
            requestListenerSlot.captured.onComplete(requestFlowTask)
            requestFlowTask
        }

        val launchFlowTask = mockk<Task<Void>>()
        every { mockReviewManager.launchReviewFlow(activity, reviewInfoMock) } returns launchFlowTask
        val reviewListenerSlot = slot<OnCompleteListener<Void>>()
        every { launchFlowTask.addOnCompleteListener(capture(reviewListenerSlot)) } answers {
            reviewListenerSlot.captured.onComplete(launchFlowTask)
            launchFlowTask
        }

        // When presentReviewIfQualified() is called
        viewModel.presentReviewIfQualified(activity)
        viewModel.presentReviewIfQualified(activity)
        advanceUntilIdle() // run all coroutines and listeners

        // Then it should go through the review flow and save the last version reviewed
        verify { mockReviewManager.requestReviewFlow() }
        verify { mockReviewManager.launchReviewFlow(activity, any()) }
        coVerify { mockDataStoreService.setLastVersionReviewed(MainViewModel.CURRENT_VERSION) }
    }
}

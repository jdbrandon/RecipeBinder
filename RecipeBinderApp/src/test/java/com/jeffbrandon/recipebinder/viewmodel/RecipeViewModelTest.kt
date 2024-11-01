package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class RecipeViewModelTest {

    private companion object {
        private const val KEY_EXTRA_ID = "extraID"
        private const val EXTRA_VAL = 1L
    }

    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var dataSource: RecipeDataSource
    @Mock private lateinit var context: Context
    private lateinit var underTest: RecipeViewModel

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler, "test dispatcher")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcher)
        whenever(context.getString(R.string.extra_recipe_id)).thenReturn(KEY_EXTRA_ID)
        whenever(dataSource.fetchRecipe(eq(EXTRA_VAL))).thenReturn(liveData { emit(TestRecipeData.RECIPE_1) })
        underTest = RecipeViewModel({ dataSource }, SavedStateHandle(mapOf(KEY_EXTRA_ID to EXTRA_VAL)), context)
    }

    @After
    fun tearDown() {
        scheduler.advanceUntilIdle()
        Dispatchers.resetMain()
    }
}

package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import com.jeffbrandon.recipebinder.testutils.getOrAwaitValue
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
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

    private val dispatcher = TestCoroutineDispatcher()
    private val scope = TestCoroutineScope(dispatcher)

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
        scope.advanceUntilIdle()
        dispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }

    @Test
    fun `test get recipe`(): Unit = runBlocking {
        scope.launch {
            val recipe = underTest.getRecipe().getOrAwaitValue()
            assertEquals(TestRecipeData.RECIPE_1, recipe)
            verify(dataSource).fetchRecipe(eq(EXTRA_VAL))
        }
    }

    @Test
    fun `test get ingredients`(): Unit = runBlocking {
        scope.launch {
            val ingredients = underTest.getIngredients().getOrAwaitValue()
            assertEquals(TestRecipeData.RECIPE_1.ingredients, ingredients)
        }
    }

    @Test
    fun `test get instructions`(): Unit = runBlocking {
        scope.launch {
            val instructions = underTest.getInstructions().getOrAwaitValue()
            assertEquals(TestRecipeData.RECIPE_1.instructions, instructions)
        }
    }
}

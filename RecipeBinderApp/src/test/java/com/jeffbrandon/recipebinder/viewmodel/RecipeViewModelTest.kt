package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.dagger.IDispatchers
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test

@ExperimentalCoroutinesApi
class RecipeViewModelTest {

    private companion object {
        private const val KEY_EXTRA_ID = "extraID"
        private const val EXTRA_VAL = 1L
    }

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var dataSource: RecipeDataSource

    @Mock
    private lateinit var context: Context
    private lateinit var underTest: RecipeViewModel

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler, "Test Dispatcher")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcher)
        whenever(context.getString(R.string.extra_recipe_id)).thenReturn(KEY_EXTRA_ID)

        whenever(dataSource.fetchRecipe(eq(EXTRA_VAL))).thenReturn(flow { emit(TestRecipeData.RECIPE_1) })
        underTest = RecipeViewModel(
            { dataSource },
            SavedStateHandle(mapOf(KEY_EXTRA_ID to EXTRA_VAL)),
            context,
            {
                object : IDispatchers {
                    override val default = dispatcher
                    override val io = dispatcher
                    override val main = dispatcher
                }
            },
        )
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test get recipe`() = runTest {
        var recipe: RecipeData? = null
        val job = launch {
            underTest.getRecipe().collect {
                recipe = it
            }
        }
        scheduler.advanceUntilIdle()
        assertEquals(TestRecipeData.RECIPE_1, recipe)
        verify(dataSource).fetchRecipe(eq(EXTRA_VAL))
        job.cancel()
    }

    @Test
    fun `test get ingredients`() = runTest {
        var ingredients: List<Ingredient> = emptyList()
        val job = launch {
            underTest.getIngredients().collect {
                ingredients = it
            }
        }
        scheduler.advanceUntilIdle()
        assertEquals(TestRecipeData.RECIPE_1.ingredients, ingredients)
        job.cancel()
    }

    @Test
    fun `test get instructions`() = runTest {
        var instructions: List<Instruction> = emptyList()
        val job = launch {
            underTest.getInstructions().collect {
                instructions = it
            }
        }
        scheduler.advanceUntilIdle()
        assertEquals(TestRecipeData.RECIPE_1.instructions, instructions)
        job.cancel()
    }
}

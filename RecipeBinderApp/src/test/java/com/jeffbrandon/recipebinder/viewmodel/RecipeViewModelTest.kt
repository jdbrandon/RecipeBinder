package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.dagger.IDispatchers
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import com.jeffbrandon.recipebinder.testutils.MainCoroutineRule
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import com.jeffbrandon.recipebinder.testutils.getOrAwaitValue
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val rule = MainCoroutineRule()

    @Mock
    private lateinit var dataSource: RecipeDataSource

    @Mock
    private lateinit var context: Context
    private lateinit var underTest: RecipeViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        whenever(context.getString(R.string.extra_recipe_id)).thenReturn(KEY_EXTRA_ID)
        whenever(dataSource.fetchRecipe(eq(EXTRA_VAL))).thenReturn(flow { emit(TestRecipeData.RECIPE_1) })
        underTest = RecipeViewModel(
            { dataSource },
            SavedStateHandle(mapOf(KEY_EXTRA_ID to EXTRA_VAL)),
            context,
            {
                object :
                    IDispatchers {
                    override val default = rule.dispatcher
                    override val io = rule.dispatcher
                    override val main = rule.dispatcher
                }
            },
        )
    }

    @Test
    fun `test get recipe`() = rule.runBlockingTest {
        val recipe = underTest.getRecipe().getOrAwaitValue()
        assertEquals(TestRecipeData.RECIPE_1, recipe)
        verify(dataSource).fetchRecipe(eq(EXTRA_VAL))
    }

    @Test
    fun `test get ingredients`() = rule.runBlockingTest {
        val ingredients = underTest.getIngredients().getOrAwaitValue()
        assertEquals(TestRecipeData.RECIPE_1.ingredients, ingredients)
    }

    @Test
    fun `test get instructions`() = rule.runBlockingTest {
        val instructions = underTest.getInstructions().getOrAwaitValue()
        assertEquals(TestRecipeData.RECIPE_1.instructions, instructions)
    }
}

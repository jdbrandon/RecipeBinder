package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.enums.UnitType
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import com.jeffbrandon.recipebinder.testutils.MainCoroutineRule
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import com.jeffbrandon.recipebinder.testutils.getOrAwaitValue
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class EditRecipeViewModelTest {

    private companion object {
        private const val KEY_EXTRA_ID = "extraID"
        private const val EXTRA_VAL = 1L
    }

    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule val coroutineRule = MainCoroutineRule()

    @Mock private lateinit var dataSource: RecipeDataSource
    @Mock private lateinit var context: Context
    private lateinit var underTest: EditRecipeViewModel
    private var currentRecipeData: RecipeData? = null
    private val testObserver = Observer<RecipeData> { t -> currentRecipeData = t }

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        whenever(context.getString(R.string.extra_recipe_id)).thenReturn(KEY_EXTRA_ID)
        whenever(dataSource.fetchRecipe(eq(EXTRA_VAL))).thenReturn(MutableLiveData(TestRecipeData.RECIPE_1))
        underTest = EditRecipeViewModel({ dataSource },
                                        SavedStateHandle(mapOf(KEY_EXTRA_ID to EXTRA_VAL)),
                                        context)
        underTest.getRecipe().observeForever(testObserver)
    }

    @After
    fun tearDown() {
        underTest.getRecipe().removeObserver(testObserver)
    }

    @Test
    fun setEditIngredient() = coroutineRule.runBlockingTest {
        val index = 1

        underTest.setEditIngredient(TestRecipeData.INGREDIENT_LIST_1[index])
        val ingredient = underTest.editIngredientLiveData.getOrAwaitValue()

        assertEquals("Got correct ingredient", TestRecipeData.INGREDIENT_1_2, ingredient)
    }

    @Test
    fun setEditInstruction() = coroutineRule.runBlockingTest {
        val index = 1

        underTest.setEditInstruction(TestRecipeData.INSTRUCTION_LIST_1[index])
        val instruction = underTest.editInstructionLiveData.getOrAwaitValue()

        assertEquals("Got correct instruction", TestRecipeData.INSTRUCTION_1_2, instruction!!)
    }

    @Test
    fun saveIngredient() = coroutineRule.runBlockingTest {
        underTest.saveIngredient(TestRecipeData.INGREDIENT_1_1)

        verify(dataSource).updateRecipe(any())
    }

    @Test
    fun saveInstruction() = coroutineRule.runBlockingTest {
        underTest.saveInstruction(TestRecipeData.INSTRUCTION_1_3)

        verify(dataSource).updateRecipe(any())
    }

    @Test
    fun convertIngredientUnits() = coroutineRule.runBlockingTest {
        underTest.setEditIngredient(TestRecipeData.INGREDIENT_1_3)
        underTest.convertIngredientUnits(UnitType.GRAM)

        val newIngredient = underTest.editIngredientLiveData.getOrAwaitValue()

        assertEquals("type", UnitType.GRAM, newIngredient!!.unit)
        assertTrue("conversion", newIngredient.amount > 453 && newIngredient.amount < 454)
    }
}

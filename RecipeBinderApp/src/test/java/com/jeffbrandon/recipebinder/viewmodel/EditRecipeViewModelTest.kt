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
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import com.jeffbrandon.recipebinder.testutils.getOrAwaitValue
import com.jeffbrandon.recipebinder.testutils.observeForTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
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

    @Mock private lateinit var dataSource: RecipeDataSource
    @Mock private lateinit var context: Context
    private lateinit var underTest: EditRecipeViewModel
    private var currentRecipeData: RecipeData? = null
    private val testObserver = Observer<RecipeData> { t -> currentRecipeData = t }

    private val dispatcher = TestCoroutineDispatcher()
    private val scope = TestCoroutineScope(dispatcher)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcher)

        whenever(context.getString(R.string.extra_recipe_id)).thenReturn(KEY_EXTRA_ID)
        whenever(dataSource.fetchRecipe(eq(EXTRA_VAL))).thenReturn(MutableLiveData(TestRecipeData.RECIPE_1))
        underTest = EditRecipeViewModel({ dataSource }, SavedStateHandle(mapOf(KEY_EXTRA_ID to EXTRA_VAL)), context)
        underTest.getRecipe().observeForever(testObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
        underTest.getRecipe().removeObserver(testObserver)
    }

    @Test
    fun setEditIngredient() = runBlocking {
        val index = 1

        underTest.setEditIngredient(TestRecipeData.INGREDIENT_LIST_1[index])
        val ingredient = underTest.editIngredientLiveData.getOrAwaitValue()

        assertEquals("Got correct ingredient", TestRecipeData.INGREDIENT_1_2, ingredient)
    }

    @Test
    fun setEditInstruction() = runBlocking {
        val index = 1

        underTest.setEditInstruction(TestRecipeData.INSTRUCTION_LIST_1[index])
        val instruction = underTest.editInstructionLiveData.getOrAwaitValue()

        assertEquals("Got correct instruction", TestRecipeData.INSTRUCTION_1_2, instruction!!)
    }

    @Test
    fun saveIngredient() = runBlocking {
        scope.launch {
            underTest.saveIngredient(TestRecipeData.INGREDIENT_1_1)
            verify(dataSource).updateRecipe(any())
        }.join()
    }

    @Test
    fun saveInstruction() = runBlocking {
        scope.launch {
            underTest.saveInstruction(TestRecipeData.INSTRUCTION_1_3)
            verify(dataSource).updateRecipe(any())
        }.join()
    }

    @Test
    fun convertIngredientUnits() = runBlocking {
        underTest.editIngredientLiveData.observeForTest {
            underTest.setEditIngredient(TestRecipeData.INGREDIENT_1_3)
            underTest.convertIngredientUnits(UnitType.GRAM)

            val newIngredient = underTest.editIngredientLiveData.getOrAwaitValue()

            assertEquals("type", UnitType.GRAM, newIngredient!!.unit)
            assertTrue("conversion", newIngredient.amount > 453 && newIngredient.amount < 454)
        }
    }

    @Test
    fun `test save metadata`(): Unit = runBlocking {
        scope.launch {
            underTest.saveMetadata("dummy", 0, listOf())
            verify(dataSource).updateRecipe(any())
        }.join()
    }
}

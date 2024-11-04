package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.dagger.IDispatchers
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.enums.UnitType
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertNull

@ExperimentalCoroutinesApi
class EditRecipeViewModelTest {

    private companion object {
        private const val KEY_EXTRA_ID = "extraID"
        private const val EXTRA_VAL = 1L
    }

    @Mock
    private lateinit var dataSource: RecipeDataSource

    @Mock
    private lateinit var context: Context
    private lateinit var underTest: EditRecipeViewModel

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler, "Test Dispatcher")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcher)

        whenever(context.getString(R.string.extra_recipe_id)).thenReturn(KEY_EXTRA_ID)
        whenever(dataSource.fetchRecipe(eq(EXTRA_VAL))).thenReturn(flow { emit(TestRecipeData.RECIPE_1) })
        underTest = EditRecipeViewModel(
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
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `set edit ingredient`() = runTestObservingRecipes {
        val index = 1

        underTest.setEditIngredient(TestRecipeData.INGREDIENT_LIST_1[index])
        var ingredient: Ingredient? = null
        val job = launch {
            underTest.editIngredientFlow.collect {
                ingredient = it
            }
        }
        scheduler.advanceUntilIdle()

        assertEquals("Got correct ingredient", TestRecipeData.INGREDIENT_1_2, ingredient)
        job.cancel()
    }

    @Test
    fun `set edit instruction`() = runTestObservingRecipes {
        val index = 1

        underTest.setEditInstruction(TestRecipeData.INSTRUCTION_LIST_1[index])
        var instruction: Instruction? = null
        val job = launch {
            underTest.editInstructionFlow.collect {
                instruction = it
            }
        }
        scheduler.advanceUntilIdle()

        assertEquals(
            "Got correct instruction", TestRecipeData.INSTRUCTION_1_2, instruction!!
        )
        job.cancel()
    }

    @Test
    fun `save ingredient`() = runTestObservingRecipes {
        underTest.setEditIngredient(TestRecipeData.INGREDIENT_LIST_1[2])
        scheduler.advanceUntilIdle()

        underTest.saveIngredient(TestRecipeData.INGREDIENT_1_1)
        scheduler.advanceUntilIdle()

        verify(dataSource).updateRecipe(any())
    }

    @Test
    fun saveInstruction() = runTestObservingRecipes {
        underTest.setEditInstruction(TestRecipeData.INSTRUCTION_LIST_1[1])
        scheduler.advanceUntilIdle()

        underTest.saveInstruction(TestRecipeData.INSTRUCTION_1_3)
        scheduler.advanceUntilIdle()

        verify(dataSource).updateRecipe(any())
    }

    @Test
    fun convertIngredientUnits() = runTestObservingRecipes {
        val ingredient = TestRecipeData.INGREDIENT_1_3
        underTest.setEditIngredient(ingredient)
        scheduler.advanceUntilIdle()
        underTest.convertIngredientUnits(ingredient.amount, ingredient.unit, UnitType.GRAM)
        scheduler.advanceUntilIdle()

        var newIngredient: Ingredient? = null
        val job = launch {
            underTest.editIngredientFlow.collect {
                newIngredient = it
            }
        }
        scheduler.advanceUntilIdle()

        assertEquals("type", UnitType.GRAM, newIngredient!!.unit)
        assertTrue("conversion", newIngredient?.amount!! > 453 && newIngredient?.amount!! < 454)
        job.cancel()
    }

    @Test
    fun `test save metadata`() = runTestObservingRecipes {
        val name = "newName"
        val time = 5
        val servings = 8
        val tags = setOf(RecipeTag.DESSERT, RecipeTag.SIDE, RecipeTag.EASY)

        underTest.saveMetadata(name, time, servings, tags)
        scheduler.advanceUntilIdle()

        val expected = TestRecipeData.RECIPE_1.copy(
            name = "NewName", cookTime = time, servings = servings, tags = tags
        )
        verify(dataSource).updateRecipe(eq(expected))
    }

    @Test
    fun `test ingredient moveTo`() = runTestObservingRecipes {
        underTest.setEditIngredient(TestRecipeData.INGREDIENT_1_3)
        scheduler.advanceUntilIdle()

        underTest.moveEditIngredientBefore(TestRecipeData.INGREDIENT_1_1)
        scheduler.advanceUntilIdle()

        verify(dataSource).updateRecipe(
            eq(
                TestRecipeData.RECIPE_1.copy(
                    ingredients = listOf(
                        TestRecipeData.INGREDIENT_1_3,
                        TestRecipeData.INGREDIENT_1_1,
                        TestRecipeData.INGREDIENT_1_2
                    )
                )
            )
        )

        var editIngredient: Ingredient? = null
        val job = launch {
            underTest.editIngredientFlow.collect {
                editIngredient = it
            }
        }
        scheduler.advanceUntilIdle()

        assertNull(editIngredient)
        job.cancel()
    }

    @Test
    fun `test instruction moveTo`() = runTestObservingRecipes {
        underTest.setEditInstruction(TestRecipeData.INSTRUCTION_1_3)
        scheduler.advanceUntilIdle()
        underTest.moveEditInstructionBefore(TestRecipeData.INSTRUCTION_1_2)
        scheduler.advanceUntilIdle()

        verify(dataSource).updateRecipe(
            eq(
                TestRecipeData.RECIPE_1.copy(
                    instructions = listOf(
                        TestRecipeData.INSTRUCTION_1_1,
                        TestRecipeData.INSTRUCTION_1_3,
                        TestRecipeData.INSTRUCTION_1_2
                    )
                )
            )
        )

        var editInstruction: Instruction? = null
        val job = launch {
            underTest.editInstructionFlow.collect {
                editInstruction = it
            }
        }
        scheduler.advanceUntilIdle()

        assertNull(editInstruction)
        job.cancel()
    }

    @Test
    fun `test delete ingredient`() = runTestObservingRecipes {
        underTest.setEditIngredient(TestRecipeData.INGREDIENT_1_2)
        scheduler.advanceUntilIdle()
        underTest.deleteEditIngredient()
        scheduler.advanceUntilIdle()

        val target = TestRecipeData.RECIPE_1.copy(
            ingredients = TestRecipeData.INGREDIENT_LIST_1.minus(
                TestRecipeData.INGREDIENT_1_2
            )
        )
        verify(dataSource).updateRecipe(eq(target))
    }

    @Test
    fun `test delete instruction`() = runTestObservingRecipes {
        underTest.setEditInstruction(TestRecipeData.INSTRUCTION_1_3)
        scheduler.advanceUntilIdle()
        underTest.deleteEditInstruction()

        val target = TestRecipeData.RECIPE_1.copy(
            instructions = TestRecipeData.INSTRUCTION_LIST_1.minus(
                TestRecipeData.INSTRUCTION_1_3
            )
        )
        scheduler.advanceUntilIdle()
        verify(dataSource).updateRecipe(eq(target))
    }

    @Test
    fun `test should warn about unsaved, begin editing, stop editing `(): Unit = runTest {
        var shouldWarn = false
        val job = launch {
            underTest.shouldWarnAboutUnsavedData().collect { v -> shouldWarn = v }
        }
        underTest.beginEditing()

        scheduler.advanceUntilIdle()
        assertTrue(shouldWarn)

        underTest.stopEditing()
        scheduler.advanceUntilIdle()
        assertFalse(shouldWarn)

        underTest.beginEditing()
        scheduler.advanceUntilIdle()
        assertTrue(shouldWarn)

        underTest.disableBackPressedWarning()
        scheduler.advanceUntilIdle()
        assertFalse(shouldWarn)
        job.cancel()
    }

    private fun runTestObservingRecipes(test: suspend TestScope.() -> Unit) = runTest {
        val job = launchRecipeCollection()
        scheduler.advanceUntilIdle()
        test()
        job.cancel()
    }

    private fun CoroutineScope.launchRecipeCollection(): Job = launch {
        underTest.getRecipe().collect()
    }
}

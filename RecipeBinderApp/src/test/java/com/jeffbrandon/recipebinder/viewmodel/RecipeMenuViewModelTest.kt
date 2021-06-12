package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.dagger.IDispatchers
import com.jeffbrandon.recipebinder.data.TagFilter
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeMenuDataSource
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import com.jeffbrandon.recipebinder.testutils.getOrAwaitValue
import com.jeffbrandon.recipebinder.util.RecipeBlobImporter
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RecipeMenuViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val recipeList = TestRecipeData.buildTestData()
    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler, "Test Dispatcher")

    @Mock
    private lateinit var dataSource: RecipeMenuDataSource

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var importer: RecipeBlobImporter
    private lateinit var underTest: RecipeMenuViewModel

    @Before
    @ExperimentalCoroutinesApi
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(dataSource.fetchAllRecipes(anyString())).thenReturn(flow { emit(recipeList) })
        underTest = RecipeMenuViewModel(context, { dataSource }, { importer }, {
            object :
                IDispatchers {
                override val default = dispatcher
                override val io = dispatcher
                override val main = dispatcher
            }
        })
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test delete`() = runTest {
        underTest.delete(TestRecipeData.RECIPE_1.recipeId!!)

        verify(dataSource).deleteRecipe(eq(TestRecipeData.RECIPE_1.recipeId!!))
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test insert`() = runTest {
        val name = "TestName"
        val insertRecipe = RecipeData().copy(name = name)

        underTest.insert(name)

        verify(dataSource).insertRecipe(eq(insertRecipe))
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test blob import`() = runTest {
        whenever(context.getString(R.string.import_success)).thenReturn("%s")
        whenever(importer.import(any())).thenReturn(TestRecipeData.RECIPE_2)

        underTest.import("")

        verify(importer).import(any())
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test blob import failure`() = runTest {
        val errorMsg = "error"
        whenever(context.getString(R.string.error_import_failed)).thenReturn(errorMsg)
        whenever(importer.import(any())).thenReturn(null)

        underTest.import("")

        val message = underTest.toastObservable().getOrAwaitValue()
        verify(importer).import(any())
        assertEquals(errorMsg, message)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - no tag filter`() = runTest {
        val recipeData = underTest.getRecipes().getOrAwaitValue()
        assertEquals(recipeList, recipeData)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter for one`() = runTest {
        underTest.filterTags(TagFilter(TestRecipeData.RECIPE_1.tags, false))

        val recipeData = underTest.getRecipes().getOrAwaitValue()
        assertEquals(listOf(TestRecipeData.RECIPE_1), recipeData)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter exclusion`() = runTest {
        underTest.filterTags(TagFilter(TestRecipeData.RECIPE_1.tags, true))

        val recipeData = underTest.getRecipes().getOrAwaitValue()
        assertEquals(listOf(TestRecipeData.RECIPE_2, TestRecipeData.RECIPE_3), recipeData)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter on empty list`() = runTest {
        underTest.filterTags(TagFilter(setOf(), false))

        val recipeData = underTest.getRecipes().getOrAwaitValue()
        assertEquals(recipeList, recipeData)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter exclusion on empty list`() = runTest {
        underTest.filterTags(TagFilter(setOf(), true))

        val recipeData = underTest.getRecipes().getOrAwaitValue()
        assertEquals(recipeList, recipeData)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter for multiple`() = runTest {
        underTest.filterTags(TagFilter(setOf(RecipeTag.EASY), false))

        val recipeData = underTest.getRecipes().getOrAwaitValue()
        underTest.filterTags(TagFilter(setOf(RecipeTag.DESSERT), false))
        val newData = underTest.getRecipes().getOrAwaitValue()

        assertEquals(listOf(TestRecipeData.RECIPE_1, TestRecipeData.RECIPE_3), recipeData)
        assertEquals(listOf(TestRecipeData.RECIPE_2, TestRecipeData.RECIPE_3), newData)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter exclude for multiple`() = runTest {
        underTest.filterTags(TagFilter(setOf(RecipeTag.EASY), true))

        val recipeData = underTest.getRecipes().getOrAwaitValue()
        underTest.filterTags(TagFilter(setOf(RecipeTag.DESSERT), true))
        val newData = underTest.getRecipes().getOrAwaitValue()

        assertEquals(listOf(TestRecipeData.RECIPE_2), recipeData)
        assertEquals(listOf(TestRecipeData.RECIPE_1), newData)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter for out everything`() = runTest {
        underTest.filterTags(TagFilter(setOf(RecipeTag.SIDE), false))

        val recipeData = underTest.getRecipes().getOrAwaitValue()

        assertEquals(listOf<RecipeData>(), recipeData)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter for out everything exclusion`() = runTest {
        underTest.filterTags(TagFilter(setOf(RecipeTag.SIDE), true))

        val recipeData = underTest.getRecipes().getOrAwaitValue()

        assertEquals(recipeList, recipeData)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test filterTags list`() = runTest {
        val testTags = TagFilter(setOf(RecipeTag.SIDE, RecipeTag.SIDE), false)

        underTest.filterTags(testTags)

        val tags = underTest.selectedTags().getOrAwaitValue()

        assertEquals(testTags, tags)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test filterTags empty list`() = runTest {
        underTest.filterTags(TagFilter(setOf(), false))

        val tags = underTest.selectedTags().getOrAwaitValue()

        assertEquals(TagFilter(setOf(), false), tags)
    }
}

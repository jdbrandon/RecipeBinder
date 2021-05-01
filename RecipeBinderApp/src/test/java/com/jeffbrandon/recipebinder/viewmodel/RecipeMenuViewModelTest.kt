package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeMenuDataSource
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import com.jeffbrandon.recipebinder.testutils.getOrAwaitValue
import com.jeffbrandon.recipebinder.util.RecipeBlobImporter
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
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

@ExperimentalCoroutinesApi
class RecipeMenuViewModelTest {

    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    private val recipeList = TestRecipeData.buildTestData()

    @Mock private lateinit var dataSource: RecipeMenuDataSource
    @Mock private lateinit var context: Context
    @Mock private lateinit var importer: RecipeBlobImporter
    private lateinit var underTest: RecipeMenuViewModel
    private val dispatcher = TestCoroutineDispatcher()
    private val scope = TestCoroutineScope(dispatcher)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcher)
        whenever(dataSource.fetchAllRecipes(anyString())).thenReturn(MutableLiveData(recipeList))
        underTest = RecipeMenuViewModel(context, { dataSource }, { importer })
    }

    @After
    fun tearDown() {
        dispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }

    @Test
    fun `test delete`() = runBlocking {
        scope.launch {
            underTest.delete(TestRecipeData.RECIPE_1.recipeId!!)

            verify(dataSource).deleteRecipe(eq(TestRecipeData.RECIPE_1.recipeId!!))
        }.join()
    }

    @Test
    fun `test insert`() = runBlocking {
        scope.launch {
            val name = "TestName"
            val insertRecipe = RecipeData().copy(name = name)

            underTest.insert(name)

            verify(dataSource).insertRecipe(eq(insertRecipe))
        }.join()
    }

    @Test
    fun `test blob import`() = runBlocking {
        scope.launch {
            whenever(context.getString(R.string.import_success)).thenReturn("%s")
            whenever(importer.import(any())).thenReturn(TestRecipeData.RECIPE_2)

            underTest.import("")
            val message = underTest.toastObservable().getOrAwaitValue()

            verify(importer).import(any())
            assertEquals(TestRecipeData.RECIPE_2.name, message)
        }.join()
    }

    @Test
    fun `test blob import failure`() = scope.runBlockingTest {
        val errorMsg = "error"
        whenever(context.getString(R.string.error_import_failed)).thenReturn(errorMsg)
        whenever(importer.import(any())).thenReturn(null)

        underTest.import("")

        val message = underTest.toastObservable().getOrAwaitValue()
        verify(importer).import(any())
        assertEquals(errorMsg, message)
    }

    @Test
    fun `test fetch recipes - no tag filter`() = scope.runBlockingTest {
        val recipeData = underTest.getRecipes().getOrAwaitValue()
        assertEquals(recipeList, recipeData)
    }

    @Test
    fun `test fetch recipes - tag filter for one`() = scope.runBlockingTest {
        underTest.filterTags(TestRecipeData.RECIPE_1.tags)

        val recipeData = underTest.getRecipes().getOrAwaitValue()
        assertEquals(listOf(TestRecipeData.RECIPE_1), recipeData)
    }

    @Test
    fun `test fetch recipes - tag filter on empty list`() = scope.runBlockingTest {
        underTest.filterTags(setOf())

        val recipeData = underTest.getRecipes().getOrAwaitValue()
        assertEquals(recipeList, recipeData)
    }

    @Test
    fun `test fetch recipes - tag filter for multiple`() = scope.runBlockingTest {
        underTest.filterTags(setOf(RecipeTag.EASY))

        val recipeData = underTest.getRecipes().getOrAwaitValue()
        underTest.filterTags(setOf(RecipeTag.DESSERT))
        val newData = underTest.getRecipes().getOrAwaitValue()

        assertEquals(listOf(TestRecipeData.RECIPE_1, TestRecipeData.RECIPE_3), recipeData)
        assertEquals(listOf(TestRecipeData.RECIPE_2, TestRecipeData.RECIPE_3), newData)
    }

    @Test
    fun `test fetch recipes - tag filter for out everything`() = scope.runBlockingTest {
        underTest.filterTags(setOf(RecipeTag.SIDE))

        val recipeData = underTest.getRecipes().getOrAwaitValue()

        assertEquals(listOf<RecipeData>(), recipeData)
    }

    @Test
    fun `test filterTags list`() {
        val testTags = setOf(RecipeTag.SIDE, RecipeTag.SIDE)
        underTest.filterTags(testTags)

        val tags = underTest.selectedTags().getOrAwaitValue()

        assertEquals(testTags, tags)
    }

    @Test
    fun `test filterTags empty list`() {
        underTest.filterTags(setOf())

        val tags = underTest.selectedTags().getOrAwaitValue()

        assertEquals(setOf<RecipeTag>(), tags)
    }
}

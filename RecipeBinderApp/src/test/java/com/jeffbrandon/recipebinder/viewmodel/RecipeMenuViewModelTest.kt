package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.dagger.IDispatchers
import com.jeffbrandon.recipebinder.data.TagFilter
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeMenuDataSource
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import com.jeffbrandon.recipebinder.util.RecipeBlobImporter
import com.jeffbrandon.recipebinder.viewmodel.Result.Loaded
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RecipeMenuViewModelTest {

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
        Dispatchers.setMain(dispatcher)
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

    @After
    @ExperimentalCoroutinesApi
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test delete`() = runTest {
        underTest.delete(TestRecipeData.RECIPE_1.recipeId!!)

        verify(dataSource).deleteRecipe(eq(TestRecipeData.RECIPE_1.recipeId))
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

        val message = underTest.toastObservable().value
        verify(importer).import(any())
        assertEquals(errorMsg, message)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - no tag filter`() = runTest {
        var recipeData: List<RecipeData>? = null
        val job = underTest.collectRecipesInScope(this) { data ->
            recipeData = data
        }
        scheduler.advanceUntilIdle()
        assertEquals(recipeList, recipeData)
        job.cancel()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter for one`() = runTest {
        underTest.filterTags(TagFilter.Include.create(TestRecipeData.RECIPE_1.tags))

        var recipeData: List<RecipeData>? = null
        val job = launch {
            underTest.collectRecipesInScope(this) { data ->
            recipeData = data
            }
        }
        scheduler.advanceUntilIdle()
        assertEquals(listOf(TestRecipeData.RECIPE_1), recipeData)
        job.cancel()
        scheduler.advanceUntilIdle()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter exclusion`() = runTest {
        underTest.filterTags(TagFilter.Exclude.create(TestRecipeData.RECIPE_1.tags))

        var recipeData: List<RecipeData>? = null
        val job = underTest.collectRecipesInScope(this) { data ->
            recipeData = data
        }
        scheduler.advanceUntilIdle()
        assertEquals(listOf(TestRecipeData.RECIPE_2, TestRecipeData.RECIPE_3), recipeData)
        job.cancel()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter on empty list`() = runTest {
        underTest.filterTags(TagFilter.Include.create(emptySet()))

        var recipeData: List<RecipeData>? = null
        val job = underTest.collectRecipesInScope(this) { data ->
            recipeData = data
        }
        scheduler.advanceUntilIdle()
        assertEquals(recipeList, recipeData)
        job.cancel()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter exclusion on empty list`() = runTest {
        underTest.filterTags(TagFilter.Exclude.create(emptySet()))

        var recipeData: List<RecipeData>? = null
        val job = underTest.collectRecipesInScope(this) { data ->
            recipeData = data
        }
        scheduler.advanceUntilIdle()
        assertEquals(recipeList, recipeData)
        job.cancel()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter for multiple`() = runTest {
        underTest.filterTags(TagFilter.Include.create(setOf(RecipeTag.EASY)))
        var recipeData: List<RecipeData>? = null
        val job = underTest.collectRecipesInScope(this) { data ->
            recipeData = data
        }
        scheduler.advanceUntilIdle()
        val easyList = recipeData

        underTest.filterTags(TagFilter.Include.create(setOf(RecipeTag.DESSERT)))
        scheduler.advanceUntilIdle()
        val newData = recipeData

        job.cancel()
        assertEquals(listOf(TestRecipeData.RECIPE_1, TestRecipeData.RECIPE_3), easyList)
        assertEquals(listOf(TestRecipeData.RECIPE_2, TestRecipeData.RECIPE_3), newData)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter exclude for multiple`() = runTest {
        underTest.filterTags(TagFilter.Exclude.create(setOf(RecipeTag.EASY)))
        var recipeData: List<RecipeData>? = null
        val job = underTest.collectRecipesInScope(this) { data ->
            recipeData = data
        }
        scheduler.advanceUntilIdle()
        val notEasy = recipeData

        underTest.filterTags(TagFilter.Exclude.create(setOf(RecipeTag.DESSERT)))
        scheduler.advanceUntilIdle()
        val newData = recipeData

        job.cancel()
        assertEquals(listOf(TestRecipeData.RECIPE_2), notEasy)
        assertEquals(listOf(TestRecipeData.RECIPE_1), newData)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter for out everything`() = runTest {
        underTest.filterTags(TagFilter.Include.create(setOf(RecipeTag.SIDE)))

        var recipeData: List<RecipeData>? = null
        val job = underTest.collectRecipesInScope(this) { data ->
            recipeData = data
        }
        scheduler.advanceUntilIdle()

        assertEquals(listOf<RecipeData>(), recipeData)
        job.cancel()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test fetch recipes - tag filter for out everything exclusion`() = runTest {
        underTest.filterTags(TagFilter.Exclude.create(setOf(RecipeTag.SIDE)))

        var recipeData: List<RecipeData>? = null
        val job = underTest.collectRecipesInScope(this) { data ->
            recipeData = data
        }
        scheduler.advanceUntilIdle()

        assertEquals(recipeList, recipeData)
        job.cancel()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test filterTags list`() = runTest {
        val testTags = TagFilter.Include.create(setOf(RecipeTag.SIDE, RecipeTag.SIDE))

        underTest.filterTags(testTags)

        val tags = underTest.selectedTags().first()

        assertEquals(testTags, tags)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test filterTags empty list`() = runTest {
        val filter = TagFilter.Include.create(setOf())
        underTest.filterTags(filter)

        val tags = underTest.selectedTags().first()

        assertEquals(filter, tags)
    }
}

@ExperimentalCoroutinesApi
private fun RecipeMenuViewModel.collectRecipesInScope(
    scope: CoroutineScope,
    updater: (List<RecipeData>) -> Unit
): Job {
    val job = scope.launch {
        getRecipes(this).collect { result ->
            when (result) {
                is Loaded<List<RecipeData>> -> updater(result.data)
                else -> Unit
            }
        }
    }
    return job
}

package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.liveData
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeMenuDataSource
import com.jeffbrandon.recipebinder.testutils.MainCoroutineRule
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import com.jeffbrandon.recipebinder.testutils.getOrAwaitValue
import com.jeffbrandon.recipebinder.testutils.observeForTest
import com.jeffbrandon.recipebinder.util.RecipeBlobImporter
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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

    @get:Rule val coroutineRule = MainCoroutineRule()

    private val recipeList = TestRecipeData.buildTestData()

    @Mock private lateinit var dataSource: RecipeMenuDataSource
    @Mock private lateinit var context: Context
    @Mock private lateinit var importer: RecipeBlobImporter
    private lateinit var underTest: RecipeMenuViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(dataSource.fetchAllRecipes(anyString())).thenReturn(liveData { recipeList })
        underTest = RecipeMenuViewModel(context, { dataSource }, { importer })
    }

    @Test
    fun `test delete`(): Unit = runBlocking {
        underTest.delete(TestRecipeData.RECIPE_1.id!!)

        verify(dataSource).deleteRecipe(eq(TestRecipeData.RECIPE_1.id!!))
    }

    @Test
    fun `test insert`() = runBlocking {
        val name = "TestName"
        val insertRecipe = RecipeData().copy(name = name)

        underTest.getRecipes().observeForTest {
            runBlocking {
                underTest.insert(name)
                verify(dataSource).insertRecipe(eq(insertRecipe))
            }
        }
    }

    @Test
    fun `test blob import`() = runBlocking {
        whenever(context.getString(R.string.import_success)).thenReturn("%s")
        whenever(importer.import(any())).thenReturn(TestRecipeData.RECIPE_2)

        underTest.import("")

        val message = underTest.toastObservable().getOrAwaitValue()

        verify(importer).import(any())
        assertEquals(message, TestRecipeData.RECIPE_2.name)
    }

    @Test
    fun `test blob import failure`() = runBlocking {
        val errorMsg = "error"
        whenever(context.getString(R.string.error_import_failed)).thenReturn(errorMsg)
        whenever(importer.import(any())).thenReturn(null)

        underTest.import("")

        val message = underTest.toastObservable().getOrAwaitValue()

        verify(importer).import(any())
        assertEquals(message, errorMsg)
    }
}

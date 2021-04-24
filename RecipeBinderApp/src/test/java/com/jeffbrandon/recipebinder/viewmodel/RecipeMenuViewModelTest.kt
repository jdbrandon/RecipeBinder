package com.jeffbrandon.recipebinder.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeMenuDataSource
import com.jeffbrandon.recipebinder.testutils.MainCoroutineRule
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import com.jeffbrandon.recipebinder.testutils.getOrAwaitValue
import com.jeffbrandon.recipebinder.testutils.observeForTest
import dagger.Lazy
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
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
class RecipeMenuViewModelTest {

    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule val coroutineRule = MainCoroutineRule()

    private val recipeList = TestRecipeData.buildTestData()

    @Mock private lateinit var lazySource: Lazy<RecipeMenuDataSource>
    @Mock private lateinit var dataSource: RecipeMenuDataSource
    private lateinit var underTest: RecipeMenuViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(lazySource.get()).thenReturn(dataSource)
        whenever(dataSource.fetchAllRecipes(any())).thenReturn(recipeList)
        underTest = RecipeMenuViewModel(lazySource).also { it.filter(null) }
    }

    @Test
    fun `test get`() = coroutineRule.runBlockingTest {
        // Simple test to make sure mocking is set up correctly
        val result = underTest.getRecipes().getOrAwaitValue()
        assertEquals("contents are correct", recipeList, result)
    }

    @Test
    fun `test delete`() = coroutineRule.runBlockingTest {
        whenever(dataSource.deleteRecipe(any())).then {
            recipeList.remove(TestRecipeData.RECIPE_1)
            TestRecipeData.ID_1.toInt()
        }

        underTest.getRecipes().observeForTest {
            underTest.delete(recipeList.indexOf(TestRecipeData.RECIPE_1))
        }
        val result = underTest.getRecipes().getOrAwaitValue()

        verify(dataSource).deleteRecipe(eq(TestRecipeData.RECIPE_1))
        assertThat("recipe was deleted", !result.contains(TestRecipeData.RECIPE_1))
    }

    @Test
    fun `test insert`() = coroutineRule.runBlockingTest {
        val name = "TestName"
        val insertRecipe = RecipeData().copy(name = name)

        underTest.getRecipes().observeForTest {
            runBlocking {
                underTest.insert(name)
                verify(dataSource).insertRecipe(eq(insertRecipe))
            }
        }
    }
}

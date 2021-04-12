package com.jeffbrandon.recipebinder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeMenuDataSource
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeMenuViewModel @Inject constructor(
    dataSource: Lazy<RecipeMenuDataSource>,
) : ViewModel() {

    private val data by lazy { dataSource.get() }
    private val recipes by lazy { MutableLiveData<List<RecipeData>>() }.also { loadRecipes() }
    private var filter: String? = null

    fun getRecipes(): LiveData<List<RecipeData>> = recipes

    fun delete(idx: Int) {
        recipes.value?.let { list ->
            viewModelScope.launch(Dispatchers.IO) {
                data.deleteRecipe(list[idx]).also { loadRecipes() }
            }
        } ?: error("null values")
    }

    /**
     * Inserts a recipe into the database and returns the deferred id of the new data item on success
     * @param recipeData to insert
     * @return deferred unique id of the new database item
     */
    suspend fun insert(recipeData: RecipeData): Long = withContext(Dispatchers.IO) {
        data.insertRecipe(recipeData).also { loadRecipes() }
    }

    fun filter(text: String?) {
        filter = text?.let { "%$text%" }
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch(Dispatchers.Main) {
            recipes.value = withContext(Dispatchers.IO) {
                filter?.let { data.fetchAllRecipes(it) } ?: data.fetchAllRecipes()
            }
        }
    }
}

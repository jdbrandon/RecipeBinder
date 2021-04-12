package com.jeffbrandon.recipebinder.viewmodel

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeMenuDataSource
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeMenuViewModel @Inject constructor(
    dataSource: Lazy<RecipeMenuDataSource>,
    private val scope: CoroutineScope,
) : ViewModel() {
    private val recipes by lazy {
        MutableLiveData<List<RecipeData>>()
    }.also { loadRecipes() }
    private val data by lazy { dataSource.get() }
    private var filter: String? = null

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    fun getRecipes(): LiveData<List<RecipeData>> = recipes

    fun delete(idx: Int) {
        recipes.value?.let { list ->
            scope.launch(Dispatchers.IO) {
                data.deleteRecipe(list[idx])
                loadRecipes()
            }
        }
    }

    /**
     * Inserts a recipe into the database and returns the deferred id of the new data item on success
     * @param recipeData to insert
     * @return deferred unique id of the new database item
     */
    fun insertAsync(recipeData: RecipeData): Deferred<Long> =
        scope.async(Dispatchers.IO) { data.insertRecipe(recipeData) }.also {
            loadRecipes()
        }

    fun filter(text: Editable?) {
        filter = text?.let { "%$text%" }
        loadRecipes()
    }

    private fun loadRecipes() {
        scope.launch(Dispatchers.Main) {
            recipes.value = withContext(Dispatchers.IO) {
                filter?.let { data.fetchAllRecipes(it) } ?: data.fetchAllRecipes()
            }
        }
    }
}

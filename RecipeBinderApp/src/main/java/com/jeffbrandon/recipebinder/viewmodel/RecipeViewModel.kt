package com.jeffbrandon.recipebinder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    dataSource: Lazy<RecipeDataSource>,
    private val scope: CoroutineScope,
) : ViewModel() {
    private companion object {
        private const val BAD_ID = -1L
    }

    private val data by lazy { dataSource.get() }
    private var recipeId: Long = BAD_ID
    private val recipe = MutableLiveData<RecipeData>()

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    fun getRecipe(): LiveData<RecipeData> = recipe

    fun setRecipe(id: Long) {
        recipeId = id
        fetchRecipe()
    }

    private fun fetchRecipe() = scope.launch(Dispatchers.IO) {
        recipeId.takeIf { it != BAD_ID }?.let {
            data.fetchRecipe(recipeId).also { recipeData ->
                withContext(Dispatchers.Main) {
                    recipe.value = recipeData
                }
            }
        }
    }
}

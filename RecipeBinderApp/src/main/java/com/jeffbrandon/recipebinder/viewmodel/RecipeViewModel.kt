package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    dataSource: Lazy<RecipeDataSource>,
    state: SavedStateHandle,
    @ApplicationContext context: Context,
) : ViewModel() {
    private val id: Long = state[context.getString(R.string.extra_recipe_id)]
        ?: error("recipeId must be in saved state, did you forget to provide it in the intent extras or fragment args bundle")
    private val data by lazy { dataSource.get() }
    private val recipe = liveData {
        emitSource(fetchRecipe(id))
    }

    fun getRecipe(): LiveData<RecipeData> = recipe

    private suspend fun fetchRecipe(id: Long) = withContext(Dispatchers.IO) {
        data.fetchRecipe(id)
    }
}

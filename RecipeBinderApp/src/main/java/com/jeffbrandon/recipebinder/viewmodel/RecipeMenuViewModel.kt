package com.jeffbrandon.recipebinder.viewmodel

import android.text.Editable
import androidx.lifecycle.*
import com.jeffbrandon.recipebinder.room.RecipeDao
import com.jeffbrandon.recipebinder.room.RecipeData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.Lazy
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class RecipeMenuViewModel @Inject constructor(dao: Lazy<RecipeDao>) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private val job = SupervisorJob()
    private val recipes: MutableLiveData<List<RecipeData>> by lazy<MutableLiveData<List<RecipeData>>> {
        MutableLiveData()
    }.also { loadRecipes() }
    private val data by lazy { dao.get() }
    private var filter: String? = null

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getRecipes(): LiveData<List<RecipeData>> = recipes

    fun delete(idx: Int) {
        recipes.value?.let { list ->
            launch(Dispatchers.IO) {
                data.deleteRecipe(list[idx])
                loadRecipes()
            }
        }
    }

    suspend fun insert(r: RecipeData): Long =
        withContext(Dispatchers.IO) { data.insertRecipe(r) }.also { loadRecipes() }

    fun filter(text: Editable?) {
        filter = text?.let { "%$text%" }
        loadRecipes()
    }

    private fun loadRecipes() {
        launch(Dispatchers.Main) {
            recipes.value = withContext(Dispatchers.IO) {
                filter?.let { data.fetchAllRecipes(it) } ?: data.fetchAllRecipes()
            }
        }
    }
}

package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.dagger.IDispatchers
import com.jeffbrandon.recipebinder.data.TagFilter
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeMenuDataSource
import com.jeffbrandon.recipebinder.util.RecipeBlobImporter
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@SuppressWarnings("TooManyFunctions")
class RecipeMenuViewModel @Inject constructor(
    @ApplicationContext context: Context,
    dataSource: Lazy<RecipeMenuDataSource>,
    lazyImportUtil: Lazy<RecipeBlobImporter>,
    lazyDispatchers: Lazy<IDispatchers>,
) : ViewModel() {

    private val data by lazy { dataSource.get() }
    private val importer by lazy { lazyImportUtil.get() }
    private val dispatchers by lazy { lazyDispatchers.get() }
    private val searchFilter = MutableStateFlow<Filter>(Filter.None)
    private val tagsFilter = MutableStateFlow<TagFilter>(TagFilter.None)

    @ExperimentalCoroutinesApi
    private val recipes: Flow<List<RecipeData>> = searchFilter.flatMapLatest { filter ->
        when (filter) {
            is Filter.None -> data.fetchAllRecipes()
            is Filter.Query -> data.fetchAllRecipes(filter.query)
        }
    }
    private val toastMessage = MutableStateFlow<String?>(null)
    private val selectedRecipeId = MutableStateFlow<Long?>(null)
    private val errorMessageContent by lazy { context.getString(R.string.error_import_failed) }
    private val importSuccessFmt by lazy { context.getString(R.string.import_success) }

    @ExperimentalCoroutinesApi
    fun getRecipes(): LiveData<List<RecipeData>> = recipes.combine(tagsFilter) { recipeList, tags ->
        recipeList.filter { recipe -> tags.match(recipe.tags) }
    }.asLiveData()

    fun toastObservable(): LiveData<String?> = toastMessage.asLiveData()

    fun selectedRecipeId(): LiveData<Long?> = selectedRecipeId.asLiveData()

    fun selectedTags(): LiveData<TagFilter> = tagsFilter.asLiveData()

    suspend fun resetToastMessage() {
        toastMessage.emit(null)
    }

    suspend fun setRecipe(id: Long) {
        selectedRecipeId.emit(id)
    }

    suspend fun delete(id: Long) = withContext(dispatchers.io) {
        data.deleteRecipe(id)
    }

    /**
     * Inserts a recipe into the database and returns the id of the new data item on success
     * @param name of the new recipe to insert
     * @return unique id of the new item
     */
    suspend fun insert(name: String): Long = withContext(dispatchers.io) {
        val recipeData = RecipeData().copy(name = name.trim().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        })
        insertInternal(recipeData)
    }

    fun filter(text: String?) {
        searchFilter.value = text?.let { Filter.Query("%$text%") } ?: Filter.None
    }

    suspend fun filterTags(tags: TagFilter) {
        tagsFilter.emit(tags)
    }

    suspend fun import(blobString: String) {
        val importedRecipe = importer.import(blobString)
        if (importedRecipe == null) {
            toastMessage.emit(errorMessageContent)
        } else {
            insertInternal(importedRecipe)
            toastMessage.emit(String.format(importSuccessFmt, importedRecipe.name))
        }
    }

    private suspend fun insertInternal(recipeData: RecipeData): Long = withContext(dispatchers.io) {
        data.insertRecipe(recipeData)
    }

    private sealed class Filter {
        object None : Filter()
        data class Query(val query: String) : Filter()
    }
}

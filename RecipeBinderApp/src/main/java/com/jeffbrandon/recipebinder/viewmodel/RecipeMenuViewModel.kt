package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.TagFilter
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeMenuDataSource
import com.jeffbrandon.recipebinder.util.RecipeBlobImporter
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@SuppressWarnings("TooManyFunctions")
class RecipeMenuViewModel @Inject constructor(
    @ApplicationContext context: Context,
    dataSource: Lazy<RecipeMenuDataSource>,
    lazyImportUtil: Lazy<RecipeBlobImporter>,
) : ViewModel() {

    private val data by lazy { dataSource.get() }
    private val importer by lazy { lazyImportUtil.get() }
    private val searchFilter = MutableLiveData<String?>(null)
    private val tagsFilter = MutableLiveData<TagFilter>()
    private val recipes: LiveData<List<RecipeData>> = searchFilter.switchMap { filter ->
        filter?.let { data.fetchAllRecipes(filter) } ?: data.fetchAllRecipes()
    }
    private val toastMessage = MutableLiveData<String?>()
    private val selectedRecipeId = MutableLiveData<Long>()
    private val errorMessageContent by lazy { context.getString(R.string.error_import_failed) }
    private val importSuccessFmt by lazy { context.getString(R.string.import_success) }

    fun getRecipes(): LiveData<List<RecipeData>> {
        val data = MediatorLiveData<List<RecipeData>>()
        data.addSource(recipes) { list ->
            data.value = list.filter { recipe -> tagsFilter.value?.match(recipe.tags) ?: true }
        }
        data.addSource(tagsFilter) { tags ->
            data.value = recipes.value?.filter { recipe -> tags.match(recipe.tags) }
        }
        return data
    }

    fun toastObservable(): LiveData<String?> = toastMessage

    fun selectedRecipeId(): LiveData<Long> = selectedRecipeId

    fun selectedTags(): LiveData<TagFilter> = tagsFilter

    fun resetToastMessage() {
        toastMessage.value = null
    }

    fun setRecipe(id: Long) {
        selectedRecipeId.value = id
    }

    suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        data.deleteRecipe(id)
    }

    /**
     * Inserts a recipe into the database and returns the id of the new data item on success
     * @param name of the new recipe to insert
     * @return unique id of the new item
     */
    suspend fun insert(name: String): Long = withContext(Dispatchers.IO) {
        val recipeData = RecipeData().copy(name = name.trim().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        })
        insertInternal(recipeData)
    }

    fun filter(text: String?) {
        searchFilter.value = text?.let { "%$text%" }
    }

    fun filterTags(tags: TagFilter) {
        tagsFilter.value = tags
    }

    suspend fun import(blobString: String) {
        val importedRecipe = importer.import(blobString)
        if (importedRecipe == null) {
            toastMessage.value = errorMessageContent
        } else {
            insertInternal(importedRecipe)
            toastMessage.value = String.format(importSuccessFmt, importedRecipe.name)
        }
    }

    private suspend fun insertInternal(recipeData: RecipeData): Long = withContext(Dispatchers.IO) {
        data.insertRecipe(recipeData)
    }
}

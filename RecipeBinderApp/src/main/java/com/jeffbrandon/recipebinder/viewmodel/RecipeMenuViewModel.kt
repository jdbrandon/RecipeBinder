package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeMenuDataSource
import com.jeffbrandon.recipebinder.util.RecipeBlobImporter
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RecipeMenuViewModel @Inject constructor(
    @ApplicationContext context: Context,
    dataSource: Lazy<RecipeMenuDataSource>,
    lazyImportUtil: Lazy<RecipeBlobImporter>,
) : ViewModel() {

    private val data by lazy { dataSource.get() }
    private val importer by lazy { lazyImportUtil.get() }
    private val searchFilter = MutableLiveData<String?>(null)
    private val recipes: LiveData<List<RecipeData>> = searchFilter.switchMap { filter ->
        filter?.let { data.fetchAllRecipes(filter) } ?: data.fetchAllRecipes()
    }
    private val toastMessage = MutableLiveData<String?>()
    private val errorMessageContent by lazy { context.getString(R.string.error_import_failed) }
    private val importSuccessFmt by lazy { context.getString(R.string.import_success) }

    fun getRecipes(): LiveData<List<RecipeData>> = recipes

    fun toastObservable(): LiveData<String?> = toastMessage

    suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        data.deleteRecipe(id)
    }

    /**
     * Inserts a recipe into the database and returns the id of the new data item on success
     * @param name of the new recipe to insert
     * @return unique id of the new item
     */
    suspend fun insert(name: String): Long = withContext(Dispatchers.IO) {
        val recipeData = RecipeData().copy(name = name.trim().capitalize(Locale.getDefault()))
        insertInternal(recipeData)
    }

    fun filter(text: String?) {
        searchFilter.value = text?.let { "%$text%" }
    }

    fun import(blobString: String) {
        viewModelScope.launch {
            val importedRecipe = importer.import(blobString)
            if (importedRecipe == null) {
                toastMessage.value = errorMessageContent
            } else {
                insertInternal(importedRecipe)
                toastMessage.value = String.format(importSuccessFmt, importedRecipe.name)
            }
            resetMessage()
        }
    }

    private suspend fun insertInternal(recipeData: RecipeData): Long = withContext(Dispatchers.IO) {
        data.insertRecipe(recipeData)
    }

    @SuppressWarnings("MagicNumber")
    private suspend fun resetMessage() = withContext(Dispatchers.Default) {
        delay(5000)
        withContext(Dispatchers.Main) {
            toastMessage.value = null
        }
    }
}

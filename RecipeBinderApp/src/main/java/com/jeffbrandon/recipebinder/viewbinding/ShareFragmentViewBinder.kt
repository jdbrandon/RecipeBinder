package com.jeffbrandon.recipebinder.viewbinding

import android.util.Base64
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.jeffbrandon.recipebinder.databinding.FragmentShareRecipeBinding
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel
import com.squareup.moshi.JsonAdapter
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.Charset
import javax.inject.Inject

class ShareFragmentViewBinder @Inject constructor(lazyAdapter: Lazy<JsonAdapter<RecipeData>>) {

    private val json by lazy { lazyAdapter.get() }
    private lateinit var binder: FragmentShareRecipeBinding

    fun bind(vm: RecipeViewModel, viewRoot: View, lifecycleOwner: LifecycleOwner) {
        binder = FragmentShareRecipeBinding.bind(viewRoot)
        vm.getRecipe().observe(lifecycleOwner) {
            vm.viewModelScope.launch {
                val b64String = encode(it)
                binder.deleteMeTestingTextView.text = b64String
            }
        }
    }

    private suspend fun encode(recipe: RecipeData): String = withContext(Dispatchers.Default) {
        val encodedRecipe =
            Base64.encodeToString(json.toJson(recipe).toByteArray(Charset.defaultCharset()),
                                  Base64.URL_SAFE)
        "recipeBinder://recipe:$encodedRecipe"
    }
}

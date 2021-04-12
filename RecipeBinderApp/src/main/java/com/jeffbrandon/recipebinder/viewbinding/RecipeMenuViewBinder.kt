package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.RecipeAdapter
import com.jeffbrandon.recipebinder.databinding.ContentRecipeMenuBinding
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.util.NavigationUtil
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

class RecipeMenuViewBinder @AssistedInject constructor(
    @Assisted private val viewModel: RecipeMenuViewModel,
    private val scope: CoroutineScope,
    @Assisted private val viewRoot: View,
    @Assisted lifecycle: LifecycleOwner,
    @Assisted vc: ViewContract,
) {
    interface ViewContract {
        fun registerContextMenu(v: View)
        fun unregisterContextMenu(v: View)
    }

    private val binder: ContentRecipeMenuBinding =
        ContentRecipeMenuBinding.bind(ViewCompat.requireViewById(viewRoot,
                                                                 R.id.recipe_content_root))

    init {

        binder.recipeRecyclerView.setHasFixedSize(true)
        viewModel.getRecipes().observe(lifecycle) {
            vc.unregisterContextMenu(binder.recipeRecyclerView)
            binder.recipeRecyclerView.adapter = RecipeAdapter(it) { id ->
                NavigationUtil.viewRecipe(viewRoot.context, id)
            }
            vc.registerContextMenu(binder.recipeRecyclerView)
        }
        binder.recipeFilterText.addTextChangedListener { text ->
            viewModel.filter(if (text.isNullOrEmpty()) null else text)
        }
        setupNewRecipeButton()
    }

    private fun setupNewRecipeButton() {
        binder.addRecipeButton.setOnClickListener {
            // Open a Dialog to create a recipe
            val newRecipeDialogContent =
                View.inflate(viewRoot.context, R.layout.dialog_create_recipe, null)
            val input = ViewCompat.requireViewById<EditText>(newRecipeDialogContent,
                                                             R.id.input_new_recipe_name)
            AlertDialog.Builder(viewRoot.context).setTitle(R.string.dialog_new_recipe_title)
                .setView(newRecipeDialogContent).setPositiveButton(R.string.create) { _, _ ->
                    val name = input.text.toString()
                    Timber.i("Creating a new recipe: $name")
                    if (name.isEmpty()) {
                        Timber.d("Recipe needs a name")
                        // make a toast
                        Toast.makeText(viewRoot.context,
                                       R.string.toast_recipe_name,
                                       Toast.LENGTH_SHORT).show()
                    } else {
                        // add basic recipe to db
                        val recipeData =
                            RecipeData().copy(name = name.capitalize(Locale.getDefault()))
                        val id = viewModel.insertAsync(recipeData)
                        scope.launch { NavigationUtil.editRecipe(viewRoot.context, id.await()) }
                    }
                }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    Timber.i("canceling recipe creation")
                    dialog.cancel()
                }.show()
        }
    }

    fun delete(position: Int): Boolean {
        viewModel.delete(position)
        return true
    }

    fun selectedPosition(): Int? = (binder.recipeRecyclerView.adapter as RecipeAdapter).position
}

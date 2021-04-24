package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.RecipeAdapter
import com.jeffbrandon.recipebinder.databinding.ContentRecipeMenuBinding
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.util.NavigationUtil
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Module
@InstallIn(FragmentComponent::class)
class RecipeMenuViewBinder @Inject constructor() {
    interface ViewContract {
        fun registerContextMenu(v: View)
        fun unregisterContextMenu(v: View)
    }

    private lateinit var viewModel: RecipeMenuViewModel
    private lateinit var viewRoot: View
    private lateinit var lifecycle: LifecycleOwner
    private lateinit var recipeList: List<RecipeData>
    private val scope: LifecycleCoroutineScope by lazy { lifecycle.lifecycleScope }

    private val binder: ContentRecipeMenuBinding by lazy {
        ContentRecipeMenuBinding.bind(ViewCompat.requireViewById(viewRoot,
                                                                 R.id.recipe_content_root))
    }

    fun bind(
        vm: RecipeMenuViewModel,
        view: View,
        lifecycle: LifecycleOwner,
        vc: ViewContract,
    ) {
        viewModel = vm
        viewRoot = view
        this.lifecycle = lifecycle

        binder.recipeRecyclerView.setHasFixedSize(true)
        viewModel.getRecipes().observe(lifecycle) {
            vc.unregisterContextMenu(binder.recipeRecyclerView)
            recipeList = it
            binder.recipeRecyclerView.adapter = RecipeAdapter(recipeList) { id ->
                NavigationUtil.viewRecipe(viewRoot.context, id)
            }
            vc.registerContextMenu(binder.recipeRecyclerView)
        }
        binder.recipeFilterText.addTextChangedListener { text ->
            viewModel.filter(if (text.isNullOrEmpty()) null else text.toString())
        }
        setupNewRecipeButton()
    }

    private fun setupNewRecipeButton() {
        binder.addRecipeButton.setOnClickListener {
            val newRecipeDialogContent =
                View.inflate(viewRoot.context, R.layout.dialog_create_recipe, null)
            val input = ViewCompat.requireViewById<EditText>(newRecipeDialogContent,
                                                             R.id.input_new_recipe_name)
            AlertDialog.Builder(viewRoot.context).setTitle(R.string.dialog_new_recipe_title)
                .setView(newRecipeDialogContent).setPositiveButton(R.string.create) { _, _ ->
                    val name = input.text.toString()
                    Timber.i("Creating a new recipe: $name")
                    if (name.isEmpty()) {
                        Snackbar.make(viewRoot, R.string.toast_recipe_name, Snackbar.LENGTH_SHORT)
                            .show()
                    } else {
                        // add basic recipe to db
                        scope.launch {
                            val id = viewModel.insert(name)
                            NavigationUtil.editRecipe(viewRoot.context, id)
                        }
                    }
                }.setNegativeButton(android.R.string.cancel) { _, _ ->
                    Timber.i("canceling recipe creation")
                }.show()
        }
    }

    fun edit(position: Int): Boolean {
        recipeList[position].id?.let { NavigationUtil.editRecipe(viewRoot.context, it) }
            ?: error("Recipe id from db was null")
        return true
    }

    fun delete(position: Int): Boolean {
        viewModel.delete(position)
        return true
    }

    fun selectedPosition(): Int? = (binder.recipeRecyclerView.adapter as RecipeAdapter).position

    fun onStart() = binder.addRecipeButton.show()

    fun onStop() = binder.addRecipeButton.hide()
}

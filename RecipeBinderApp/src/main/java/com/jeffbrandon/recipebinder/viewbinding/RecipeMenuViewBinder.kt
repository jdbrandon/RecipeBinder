package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.RecipeAdapter
import com.jeffbrandon.recipebinder.data.TagFilter
import com.jeffbrandon.recipebinder.databinding.FragmentRecipeMenuBinding
import com.jeffbrandon.recipebinder.util.NavigationUtil
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel
import com.jeffbrandon.recipebinder.widgets.TagsFilterDialog
import dagger.Lazy
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Module
@InstallIn(FragmentComponent::class)
class RecipeMenuViewBinder @Inject constructor(private val dialog: Lazy<TagsFilterDialog>) {
    interface ViewContract {
        fun registerContextMenu(v: View)
        fun unregisterContextMenu(v: View)
    }

    private val filterDialog by lazy { dialog.get() }
    private lateinit var viewModel: RecipeMenuViewModel
    private lateinit var viewRoot: View
    private var selectedRecipeId: Long? = null
    private var tagFilter: TagFilter? = null
    private val defaultTagFilter = TagFilter(setOf(), false)

    private lateinit var binder: FragmentRecipeMenuBinding

    fun bind(
        vm: RecipeMenuViewModel,
        view: View,
        lifecycle: LifecycleOwner,
        vc: ViewContract,
    ) {
        viewModel = vm
        viewRoot = view
        binder = FragmentRecipeMenuBinding.bind(ViewCompat.requireViewById(view, R.id.recipe_content_root))

        binder.recipeRecyclerView.setHasFixedSize(true)
        viewModel.getRecipes().observe(lifecycle) {
            vc.unregisterContextMenu(binder.recipeRecyclerView)
            binder.recipeRecyclerView.adapter = RecipeAdapter(it, viewModel)
            vc.registerContextMenu(binder.recipeRecyclerView)
        }
        viewModel.selectedRecipeId().observe(lifecycle) {
            selectedRecipeId = it
        }
        viewModel.selectedTags().observe(lifecycle) { tags ->
            tagFilter = tags
        }
        binder.recipeFilterText.addTextChangedListener { text ->
            viewModel.filter(if (text.isNullOrEmpty()) null else text.toString())
        }
        binder.filterButton.setOnClickListener {
            filterDialog.show(view.context, tagFilter ?: defaultTagFilter) { tags ->
                viewModel.filterTags(tags)
            }
        }
        binder.clearFiltersButton.setOnClickListener {
            viewModel.filterTags(defaultTagFilter)
        }
        setupNewRecipeButton()
    }

    private fun setupNewRecipeButton() {
        binder.addRecipeButton.setOnClickListener {
            val newRecipeDialogContent = View.inflate(viewRoot.context, R.layout.dialog_create_recipe, null)
            val input = ViewCompat.requireViewById<EditText>(newRecipeDialogContent, R.id.input_new_recipe_name)
            AlertDialog.Builder(viewRoot.context).setTitle(R.string.dialog_new_recipe_title)
                .setView(newRecipeDialogContent).setPositiveButton(R.string.create) { _, _ ->
                    val name = input.text.toString()
                    Timber.i("Creating a new recipe: $name")
                    if (name.isEmpty()) {
                        Snackbar.make(viewRoot, R.string.toast_recipe_name, Snackbar.LENGTH_SHORT).show()
                    } else {
                        // add basic recipe to db
                        with(viewModel) {
                            viewModelScope.launch {
                                val id = insert(name)
                                NavigationUtil.editRecipe(viewRoot.context, id)
                            }
                        }
                    }
                }.setNegativeButton(android.R.string.cancel) { _, _ ->
                    Timber.i("canceling recipe creation")
                }.show()
        }
    }

    fun edit(): Boolean {
        selectedRecipeId?.let {
            NavigationUtil.editRecipe(viewRoot.context, it)
        } ?: error("Recipe id from db was null")
        return true
    }

    fun delete(): Boolean {
        selectedRecipeId?.let { id ->
            with(viewModel) {
                viewModelScope.launch { delete(id) }
            }
        }
        return true
    }

    fun onStart() = binder.addRecipeButton.show()

    fun onStop() = binder.addRecipeButton.hide()
}

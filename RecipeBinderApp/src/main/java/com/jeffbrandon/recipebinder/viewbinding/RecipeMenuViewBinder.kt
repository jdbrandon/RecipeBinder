package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.RecipeAdapter
import com.jeffbrandon.recipebinder.databinding.ActivityRecipeMenuBinding
import com.jeffbrandon.recipebinder.databinding.ContentRecipeMenuBinding
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.util.NavigationUtil
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class RecipeMenuViewBinder constructor(
    private val viewModel: RecipeMenuViewModel,
    private val activity: AppCompatActivity,
    vc: ViewContract,
) {
    interface ViewContract {
        fun registerContextMenu(v: View)
        fun unregisterContextMenu(v: View)
    }

    private val binder: ContentRecipeMenuBinding

    init {
        val viewRoot = ActivityRecipeMenuBinding.inflate(activity.layoutInflater).root
        activity.setContentView(viewRoot)
        binder = ContentRecipeMenuBinding.bind(
            ViewCompat.requireViewById(
                viewRoot,
                R.id.recipe_content_root
            )
        )

        binder.recipeRecyclerView.setHasFixedSize(true)
        viewModel.getRecipes().observe(activity) {
            vc.unregisterContextMenu(binder.recipeRecyclerView)
            binder.recipeRecyclerView.adapter = RecipeAdapter(it) { id ->
                NavigationUtil.viewRecipe(activity, id)
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
            //Open a Dialog to create a recipe
            val newRecipeDialogContent = View.inflate(activity, R.layout.dialog_create_recipe, null)
            val input = ViewCompat.requireViewById<EditText>(
                newRecipeDialogContent,
                R.id.input_new_recipe_name
            )
            AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_new_recipe_title)
                .setView(newRecipeDialogContent)
                .setPositiveButton(R.string.create) { _, _ ->
                    val name = input.text.toString()
                    Timber.i("Creating a new recipe: $name")
                    if (name.isEmpty()) {
                        Timber.d("Recipe needs a name")
                        //make a toast
                        Toast.makeText(
                            activity,
                            R.string.toast_recipe_name, Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.launch {
                            //add basic recipe to db
                            val recipeData =
                                RecipeData().copy(name = name.capitalize(Locale.getDefault()))
                            val id = viewModel.insert(recipeData)
                            NavigationUtil.editRecipe(activity, id)
                        }
                    }
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    Timber.i("canceling recipe creation")
                    dialog.cancel()
                }
                .show()
        }
    }

    fun delete(position: Int): Boolean {
        viewModel.delete(position)
        return true
    }

    fun selectedPosition(): Int? = (binder.recipeRecyclerView.adapter as RecipeAdapter).position

}

package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import androidx.activity.viewModels
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.enums.RecipeMode
import com.jeffbrandon.recipebinder.fragments.EditRecipeFragment
import com.jeffbrandon.recipebinder.fragments.ViewRecipeFragment
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NewRecipeActivity : NewRecipeAppActivity() {
    private companion object {
        private const val BAD_ID = -1L
    }

    private val viewRecipeFragment by lazy { ViewRecipeFragment() }
    private val editRecipeFragment by lazy { EditRecipeFragment() }
    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        initViewModel()
        initFragment()
    }

    private fun initViewModel() {
        val recipeId =
            intent.getLongExtra(getString(R.string.extra_recipe_id), BAD_ID).takeIf { it != BAD_ID }
                ?: error("Recipe id not supplied")
        viewModel.setRecipe(recipeId)
    }

    private fun initFragment() {
        val fragment =
            when (intent.getSerializableExtra(getString(R.string.extra_view_mode)) as? RecipeMode) {
                RecipeMode.VIEW -> viewRecipeFragment.also { Timber.i("got view fragment") }
                RecipeMode.EDIT -> editRecipeFragment.also { Timber.i("got edit fragment") }
                else -> viewRecipeFragment.also { Timber.w("unable to get RecipeMode, falling back to VIEW") }
            }
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()
    }
}

package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.enums.RecipeMode
import com.jeffbrandon.recipebinder.fragments.EditRecipeFragment
import com.jeffbrandon.recipebinder.fragments.ViewRecipeFragment
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewRecipeActivity : NewRecipeAppActivity() {
    private companion object {
        private const val BAD_ID = -1L
    }

    private val viewRecipeFragment by lazy { ViewRecipeFragment() }
    private val editRecipeFragment: Fragment by lazy { EditRecipeFragment() }
    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        val recipeId =
            intent.getLongExtra(getString(R.string.extra_recipe_id), BAD_ID).takeIf { it != BAD_ID }
                ?: error("Recipe id not supplied")
        viewModel.setRecipe(recipeId)
        val fragment =
            when (RecipeMode.fromOrdinal(intent.getIntExtra(getString(R.string.extra_view_mode),
                                                            -1))) {
                RecipeMode.VIEW -> viewRecipeFragment
                RecipeMode.EDIT -> editRecipeFragment
            }
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
    }
}
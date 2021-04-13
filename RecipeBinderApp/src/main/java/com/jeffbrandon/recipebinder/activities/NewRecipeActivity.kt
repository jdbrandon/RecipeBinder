package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.enums.RecipeMode
import com.jeffbrandon.recipebinder.fragments.EditRecipeFragment
import com.jeffbrandon.recipebinder.fragments.ViewRecipeFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NewRecipeActivity : NewRecipeAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        initFragment()
    }

    private fun initFragment() {
        val fragment =
            when (intent.getSerializableExtra(getString(R.string.extra_view_mode)) as? RecipeMode) {
                RecipeMode.VIEW -> ViewRecipeFragment::class.java.also { Timber.i("got view fragment") }
                RecipeMode.EDIT -> EditRecipeFragment::class.java.also { Timber.i("got edit fragment") }
                else ->
                    ViewRecipeFragment::class.java.also { Timber.w("unable to get RecipeMode, falling back to VIEW") }
            }
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, null)
            .commit()
    }
}

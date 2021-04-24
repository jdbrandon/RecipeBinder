package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import androidx.activity.viewModels
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.enums.RecipeMode
import com.jeffbrandon.recipebinder.viewbinding.RecipeActivityBinder
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewRecipeActivity : NewRecipeAppActivity() {

    @Inject lateinit var binder: RecipeActivityBinder
    private val viewModel: EditRecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        val mode = intent.getSerializableExtra(getString(R.string.extra_view_mode)) as? RecipeMode
        binder.bind(viewModel, findViewById(R.id.fragment_container), supportFragmentManager, mode)
    }

    override fun onBackPressed() {
        binder.onBackPressed {
            super.onBackPressed()
        }
    }
}

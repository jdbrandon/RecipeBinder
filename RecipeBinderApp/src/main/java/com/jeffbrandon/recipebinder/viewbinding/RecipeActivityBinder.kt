package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.enums.RecipeMode
import com.jeffbrandon.recipebinder.fragments.EditRecipeFragment
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import timber.log.Timber
import javax.inject.Inject

class RecipeActivityBinder @Inject constructor() {
    private lateinit var viewModel: EditRecipeViewModel
    private lateinit var viewRoot: View

    fun bind(vm: EditRecipeViewModel, view: View, fm: FragmentManager, mode: RecipeMode? = null) {
        viewModel = vm
        viewRoot = view
        initFragment(fm, mode)
    }

    fun onBackPressed(backPressedCallback: () -> Unit) {
        if (viewModel.shouldWarnAboutUnsavedData()) {
            Snackbar.make(viewRoot, R.string.abandon_warning_text, Snackbar.LENGTH_LONG).setAction(R.string.abandon) {
                backPressedCallback()
            }.show()
            return
        }
        backPressedCallback()
    }

    private fun initFragment(fragmentManager: FragmentManager, recipeMode: RecipeMode?) {
        when (recipeMode) {
            RecipeMode.EDIT -> EditRecipeFragment::class.java.also { editFragment ->
                Timber.i("got edit fragment")
                fragmentManager.beginTransaction().add(R.id.fragment_container, editFragment, null).commit()
            }
            RecipeMode.VIEW -> Timber.i("got view fragment")
            else -> Timber.w("unable to get RecipeMode, falling back to VIEW")
        }
    }
}

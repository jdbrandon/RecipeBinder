package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.enums.RecipeMode
import com.jeffbrandon.recipebinder.fragments.EditRecipeFragment
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class RecipeActivityBinder @Inject constructor() {
    private lateinit var viewModel: EditRecipeViewModel
    private lateinit var viewRoot: View
    private lateinit var backHandler: OnBackPressedCallback

    fun bind(
        vm: EditRecipeViewModel,
        view: View,
        fm: FragmentManager,
        owner: LifecycleOwner,
        backDispatcher: OnBackPressedDispatcher,
        mode: RecipeMode? = null
    ) {
        viewModel = vm
        viewRoot = view

        backHandler = object : OnBackPressedCallback(false /* enabled */) {
            override fun handleOnBackPressed() {
                onBackPressed { backDispatcher.onBackPressed() }
            }
        }
        backDispatcher.addCallback(owner, backHandler)
        owner.run {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.shouldWarnAboutUnsavedData().collect { shouldWarn ->
                        backHandler.isEnabled = shouldWarn
                    }
                }
            }
        }
        initFragment(fm, mode)
    }

    private fun onBackPressed(backPressedCallback: () -> Unit) {
        viewModel.disableBackPressedWarning()
        Snackbar.make(viewRoot, R.string.abandon_warning_text, Snackbar.LENGTH_LONG)
            .setAction(R.string.abandon) {
                backPressedCallback()
            }.show()
    }

    private fun initFragment(fragmentManager: FragmentManager, recipeMode: RecipeMode?) {
        when (recipeMode) {
            RecipeMode.EDIT -> EditRecipeFragment::class.java.also { editFragment ->
                Timber.i("got edit fragment")
                fragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.fragment_container, editFragment, null)
                }
            }

            RecipeMode.VIEW -> Timber.i("got view fragment")
            else -> Timber.w("unable to get RecipeMode, falling back to VIEW")
        }
    }
}

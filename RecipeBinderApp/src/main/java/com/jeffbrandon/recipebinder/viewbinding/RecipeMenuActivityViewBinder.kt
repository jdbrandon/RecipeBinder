package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.fragments.AboutFragment
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel
import com.jeffbrandon.recipebinder.widgets.ImportRecipeBlobDialog
import dagger.Lazy
import javax.inject.Inject

class RecipeMenuActivityViewBinder @Inject constructor(dialog: Lazy<ImportRecipeBlobDialog>) {

    private val importDialog by lazy { dialog.get() }
    private lateinit var viewRoot: View
    private lateinit var viewModel: RecipeMenuViewModel

    fun bind(
        view: View,
        vm: RecipeMenuViewModel,
        lifecycleOwner: LifecycleOwner,
    ) {
        viewRoot = view
        viewModel = vm

        viewModel.toastObservable().observe(lifecycleOwner) { message ->
            if (message != null) {
                Snackbar.make(viewRoot, message, Snackbar.LENGTH_SHORT).show()
                viewModel.resetToastMessage()
            }
        }
    }

    fun import(): Boolean {
        importDialog.show(viewRoot.context, viewModel)
        return true
    }

    fun about(fm: FragmentManager): Boolean {
        fm.commit {
            setReorderingAllowed(true)
            add(R.id.fragment_container, AboutFragment::class.java, null)
            addToBackStack(null)
        }
        return true
    }
}

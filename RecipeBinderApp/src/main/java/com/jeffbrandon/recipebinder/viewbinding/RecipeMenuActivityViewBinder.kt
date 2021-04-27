package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.fragments.MenuFragment
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
        fm: FragmentManager,
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

        fm.beginTransaction().add(R.id.fragment_container, MenuFragment::class.java, null).commit()
    }

    fun import(): Boolean {
        importDialog.show(viewRoot.context, viewModel)
        return true
    }

    fun settings(): Boolean {
        // TODO show a Settings/About page
        return true
    }
}

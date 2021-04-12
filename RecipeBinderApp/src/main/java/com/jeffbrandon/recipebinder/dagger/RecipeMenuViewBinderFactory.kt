package com.jeffbrandon.recipebinder.dagger

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.viewbinding.RecipeMenuViewBinder
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface RecipeMenuViewBinderFactory {
    fun create(
        viewModel: RecipeMenuViewModel,
        view: View,
        owner: LifecycleOwner,
        viewContract: RecipeMenuViewBinder.ViewContract,
    ): RecipeMenuViewBinder
}

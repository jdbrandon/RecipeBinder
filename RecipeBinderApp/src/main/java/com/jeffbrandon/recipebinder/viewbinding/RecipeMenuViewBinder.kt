package com.jeffbrandon.recipebinder.viewbinding

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.jeffbrandon.recipebinder.databinding.ContentRecipeMenuBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class RecipeMenuViewBinder @AssistedInject constructor(
    @Assisted private val activity: AppCompatActivity,
    @Assisted inflater: LayoutInflater
) {
    private val binder = ContentRecipeMenuBinding.inflate(inflater)

    init {
        activity.setContentView(binder.root)
    }
}
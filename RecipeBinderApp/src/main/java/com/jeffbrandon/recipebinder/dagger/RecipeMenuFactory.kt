package com.jeffbrandon.recipebinder.dagger

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.jeffbrandon.recipebinder.viewbinding.RecipeMenuViewBinder
import dagger.assisted.AssistedFactory

@AssistedFactory
interface RecipeMenuFactory {
    fun create(activity: AppCompatActivity, inflater: LayoutInflater): RecipeMenuViewBinder
}
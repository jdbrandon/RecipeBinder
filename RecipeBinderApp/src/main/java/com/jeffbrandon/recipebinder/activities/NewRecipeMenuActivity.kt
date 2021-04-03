package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import com.jeffbrandon.recipebinder.dagger.RecipeMenuFactory
import com.jeffbrandon.recipebinder.viewbinding.RecipeMenuViewBinder
import javax.inject.Inject

class NewRecipeMenuActivity : NewRecipeAppActivity() {

    @Inject
    lateinit var binderFactory: RecipeMenuFactory
    private lateinit var viewBinder: RecipeMenuViewBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinder = binderFactory.create(this, layoutInflater)
    }
}

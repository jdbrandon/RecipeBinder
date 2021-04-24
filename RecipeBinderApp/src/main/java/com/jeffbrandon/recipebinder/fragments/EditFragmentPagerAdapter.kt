package com.jeffbrandon.recipebinder.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.jeffbrandon.recipebinder.R

class EditFragmentPagerAdapter(activity: FragmentActivity) : FragmentPagerAdapter(activity) {
    companion object {
        val tabNameResourceIdList =
            listOf(R.string.metadata, R.string.ingredients, R.string.instructions)
    }

    override val fragments: List<Lazy<Fragment>> = listOf(lazy { EditRecipeMetadataFragment() },
                                                          lazy { EditRecipeIngredientsFragment() },
                                                          lazy { EditRecipeInstructionsFragment() })
}

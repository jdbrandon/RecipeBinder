package com.jeffbrandon.recipebinder.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class EditFragmentPagerAdapter(activity: FragmentActivity) : FragmentPagerAdapter(activity) {
    override val fragments: List<Lazy<Fragment>> =
        listOf(lazy { EditRecipeMetadataFragment() }, lazy { EditRecipeIngredientsFragment() })
}

package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeBinding
import com.jeffbrandon.recipebinder.fragments.EditFragmentPagerAdapter
import javax.inject.Inject

class EditRecipeViewBinder @Inject constructor() {

    private companion object {
        private val tabNameResourceIdList = listOf(R.string.metadata, R.string.ingredients)
    }

    private lateinit var binder: FragmentEditRecipeBinding

    fun bind(
        activity: FragmentActivity,
        view: View,
    ) {
        binder = FragmentEditRecipeBinding.bind(view)
        with(binder) {
            fragmentPager.adapter = EditFragmentPagerAdapter(activity)
            TabLayoutMediator(navigationTabs, fragmentPager) { tab, pos ->
                tab.text = view.context.getString(tabNameResourceIdList[pos])
            }.attach()
        }
    }
}

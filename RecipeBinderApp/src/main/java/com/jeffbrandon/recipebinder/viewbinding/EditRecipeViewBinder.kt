package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeBinding
import com.jeffbrandon.recipebinder.fragments.EditFragmentPagerAdapter
import com.jeffbrandon.recipebinder.fragments.FragmentPagerAdapter
import com.jeffbrandon.recipebinder.fragments.Saveable
import com.jeffbrandon.recipebinder.fragments.ViewRecipeFragment
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
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
            saveRecipeButton.setOnClickListener {
                ((fragmentPager.adapter as FragmentPagerAdapter).fragments[0].value as? Saveable)?.save()
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ViewRecipeFragment::class.java, null).commit()
            }
        }
    }
}

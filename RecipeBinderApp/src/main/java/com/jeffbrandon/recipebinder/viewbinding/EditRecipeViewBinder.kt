package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeBinding
import com.jeffbrandon.recipebinder.fragments.EditFragmentPagerAdapter
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import javax.inject.Inject

class EditRecipeViewBinder @Inject constructor() {

    private companion object {
        private val tabNameResourceIdList = listOf(R.string.metadata, R.string.ingredients)
    }

    private lateinit var binder: FragmentEditRecipeBinding

    fun bind(
        viewModel: EditRecipeViewModel,
        activity: FragmentActivity,
        view: View,
        lifecycle: LifecycleOwner,
    ) {
        binder = FragmentEditRecipeBinding.bind(view)
        with(binder) {
            fragmentPager.adapter = EditFragmentPagerAdapter(activity)
            viewModel.pageIndexLiveData.observe(lifecycle) {
                fragmentPager.setCurrentItem(it, true)
            }
            TabLayoutMediator(navigationTabs, fragmentPager) { tab, pos ->
                tab.text = view.context.getString(tabNameResourceIdList[pos])
            }.attach()
        }
    }
}

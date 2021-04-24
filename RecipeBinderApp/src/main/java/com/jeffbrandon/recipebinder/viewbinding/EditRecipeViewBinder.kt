package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeBinding
import com.jeffbrandon.recipebinder.fragments.EditFragmentPagerAdapter
import com.jeffbrandon.recipebinder.fragments.FragmentPagerAdapter
import com.jeffbrandon.recipebinder.fragments.Savable
import com.jeffbrandon.recipebinder.fragments.ViewRecipeFragment
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import javax.inject.Inject

class EditRecipeViewBinder @Inject constructor() {

    private lateinit var viewModel: EditRecipeViewModel
    private lateinit var binder: FragmentEditRecipeBinding

    fun bind(
        vm: EditRecipeViewModel,
        activity: FragmentActivity,
        view: View,
    ) {
        viewModel = vm
        binder = FragmentEditRecipeBinding.bind(view)
        with(binder) {
            fragmentPager.adapter = EditFragmentPagerAdapter(activity)

            TabLayoutMediator(navigationTabs, fragmentPager) { tab, pos ->
                tab.text =
                    view.context.getString(EditFragmentPagerAdapter.tabNameResourceIdList[pos])
            }.attach()

            saveRecipeButton.setOnClickListener {
                ((fragmentPager.adapter as FragmentPagerAdapter).fragments[0].value as? Savable)?.save()
                with(activity.supportFragmentManager) {
                    when (backStackEntryCount) {
                        // Handles case where we began editing from menu fragment
                        0 -> beginTransaction().replace(R.id.fragment_container,
                                                        ViewRecipeFragment::class.java,
                                                        null).commit()
                        else -> popBackStack()
                    }
                }
            }
        }
    }

    fun onResume() {
        viewModel.beginEditing()
    }

    fun onPause() {
        viewModel.stopEditing()
    }
}

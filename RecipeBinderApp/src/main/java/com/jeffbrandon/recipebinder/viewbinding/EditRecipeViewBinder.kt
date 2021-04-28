package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeBinding
import com.jeffbrandon.recipebinder.fragments.EditFragmentPagerAdapter
import com.jeffbrandon.recipebinder.fragments.FragmentPagerAdapter
import com.jeffbrandon.recipebinder.fragments.Savable
import com.jeffbrandon.recipebinder.fragments.ViewRecipeFragment
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditRecipeViewBinder @Inject constructor() {

    private lateinit var viewModel: EditRecipeViewModel
    private lateinit var binder: FragmentEditRecipeBinding
    private var selectedPage: Int? = null

    fun bind(
        vm: EditRecipeViewModel,
        activity: FragmentActivity,
        view: View,
    ) {
        viewModel = vm
        binder = FragmentEditRecipeBinding.bind(view)
        with(binder) {
            val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    selectedPage?.let { viewModel.viewModelScope.launch { fragmentPager.adapter?.save(it) } }
                    selectedPage = position
                }
            }
            fragmentPager.adapter = EditFragmentPagerAdapter(activity)
            fragmentPager.registerOnPageChangeCallback(pageChangeCallback)

            TabLayoutMediator(navigationTabs, fragmentPager) { tab, pos ->
                tab.text = view.context.getString(EditFragmentPagerAdapter.tabNameResourceIdList[pos])
            }.attach()

            saveRecipeButton.setOnClickListener {
                selectedPage?.let { viewModel.viewModelScope.launch { fragmentPager.adapter?.save(it) } }
                with(activity.supportFragmentManager) {
                    when (backStackEntryCount) {
                        // Handles case where we began editing from menu fragment
                        0 -> beginTransaction().replace(R.id.fragment_container, ViewRecipeFragment::class.java, null)
                            .commit()
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

    private suspend fun RecyclerView.Adapter<*>.save(i: Int) {
        ((this as FragmentPagerAdapter).fragments[i].value as? Savable)?.let {
            withContext(Dispatchers.IO) { it.save() }
        }
    }
}

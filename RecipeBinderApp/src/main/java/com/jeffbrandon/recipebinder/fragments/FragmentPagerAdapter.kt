package com.jeffbrandon.recipebinder.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

abstract class FragmentPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    abstract val fragments: List<Lazy<Fragment>>

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position].value
}

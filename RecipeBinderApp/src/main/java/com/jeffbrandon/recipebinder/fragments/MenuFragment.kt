package com.jeffbrandon.recipebinder.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewbinding.RecipeMenuViewBinder
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MenuFragment : Fragment(R.layout.content_recipe_menu), RecipeMenuViewBinder.ViewContract {

    @Inject lateinit var viewBinder: RecipeMenuViewBinder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vm: RecipeMenuViewModel by activityViewModels()
        viewBinder.bind(vm, view, this, this)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Timber.i("${item.itemId} ${item.menuInfo}")
        val position = viewBinder.selectedPosition()
        return when (item.itemId) {
            R.id.recipe_menu_delete -> position?.let { viewBinder.delete(it) } ?: true
            else -> super.onContextItemSelected(item)
        }
    }

    override fun registerContextMenu(v: View) {
        Timber.i("registering for context menu ${v.contentDescription}")
        registerForContextMenu(v)
    }

    override fun unregisterContextMenu(v: View) = unregisterForContextMenu(v)
}
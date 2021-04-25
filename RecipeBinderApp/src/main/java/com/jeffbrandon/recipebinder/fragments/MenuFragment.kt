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
class MenuFragment : Fragment(R.layout.fragment_recipe_menu), RecipeMenuViewBinder.ViewContract {

    @Inject lateinit var binder: RecipeMenuViewBinder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vm: RecipeMenuViewModel by activityViewModels()
        binder.bind(vm, view, this, this)
    }

    override fun onStart() {
        super.onStart()
        binder.onStart()
    }

    override fun onStop() {
        super.onStop()
        binder.onStop()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = binder.selectedPosition()
        return when (item.itemId) {
            R.id.recipe_menu_delete -> position?.let { binder.delete(it) } ?: true
            R.id.recipe_menu_edit -> position?.let { binder.edit(it) } ?: true
            else -> super.onContextItemSelected(item)
        }
    }

    override fun registerContextMenu(v: View) {
        Timber.i("registering for context menu ${v.contentDescription}")
        registerForContextMenu(v)
    }

    override fun unregisterContextMenu(v: View) = unregisterForContextMenu(v)
}

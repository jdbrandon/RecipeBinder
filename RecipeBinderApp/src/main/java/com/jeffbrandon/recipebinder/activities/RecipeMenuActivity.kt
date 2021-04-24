package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.View
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.fragments.MenuFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeMenuActivity : RecipeAppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_host)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, MenuFragment::class.java, null).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?,
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.recipe_menu, menu)
    }
}

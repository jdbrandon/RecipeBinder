package com.jeffbrandon.recipebinder.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeDao
import com.jeffbrandon.recipebinder.room.RecipeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

abstract class RecipeAppActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var deferredDb: Deferred<RecipeDao>
    protected val recipePersistentData by lazy { runBlocking { deferredDb.await() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = SupervisorJob()
        deferredDb = async(Dispatchers.IO) { RecipeDatabase.getInstance(this@RecipeAppActivity).recipeDao() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    fun navigateToEditRecipeActivity(id: Long) {
        startActivity(getViewActivityIntent(id).apply {
            putExtra(getString(R.string.view_mode_extra),
                     ViewRecipeActivity.EDIT)
        })
    }

    fun navigateToViewRecipeActivity(id: Long) {
        startActivity(getViewActivityIntent(id))
    }

    fun getViewActivityIntent(id: Long): Intent {
        return Intent(this, ViewRecipeActivity::class.java).apply {
            putExtra(getString(R.string.database_recipe_id), id)
        }
    }

    protected fun hideKeyboard() {
        val view = findViewById<View>(android.R.id.content)
        if(view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

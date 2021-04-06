package com.jeffbrandon.recipebinder.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeDao
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * @deprecated use [NewRecipeAppActivity]
 */
@AndroidEntryPoint
abstract class RecipeAppActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = coroutineContextImpl

    @Inject lateinit var job: Job
    @Inject lateinit var coroutineContextImpl: CoroutineContext
    @Inject lateinit var db: Lazy<RecipeDao>
    private lateinit var deferredDb: Deferred<RecipeDao>
    protected val recipePersistentData by lazy { runBlocking(Dispatchers.IO) { deferredDb.await() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        deferredDb = async(Dispatchers.IO) { db.get() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
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
            putExtra(getString(R.string.view_mode_extra), ViewRecipeActivity.EDIT_TAGS)
        })
    }

    fun navigateToViewRecipeActivity(id: Long) {
        startActivity(getViewActivityIntent(id))
    }

    private fun getViewActivityIntent(id: Long): Intent {
        return Intent(this, ViewRecipeActivity::class.java).apply {
            putExtra(getString(R.string.database_recipe_id), id)
        }
    }

    protected fun hideKeyboard() {
        val view = findViewById<View>(android.R.id.content)
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

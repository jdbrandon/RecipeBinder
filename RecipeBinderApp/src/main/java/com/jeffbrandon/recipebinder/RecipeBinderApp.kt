package com.jeffbrandon.recipebinder

import android.app.Application
import timber.log.Timber

class RecipeBinderApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("Tree planted")
    }
}

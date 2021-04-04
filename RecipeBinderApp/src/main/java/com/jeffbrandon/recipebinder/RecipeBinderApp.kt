package com.jeffbrandon.recipebinder

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class RecipeBinderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
        Timber.i("Tree planted")
    }
}

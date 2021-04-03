package com.jeffbrandon.recipebinder

import android.app.Application
import com.jeffbrandon.recipebinder.activities.NewRecipeMenuActivity
import com.jeffbrandon.recipebinder.dagger.AppModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject


class RecipeBinderApp : Application(), HasAndroidInjector {
    @Component(modules = [AndroidInjectionModule::class, AppModule::class])
    interface RecipeBinderApplicationDaggerComponent {
        fun inject(app: RecipeBinderApp)
        fun inject(menuActivity: NewRecipeMenuActivity)
    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        DaggerRecipeBinderApp_RecipeBinderApplicationDaggerComponent.create().inject(this)
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
        Timber.i("Tree planted")
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector


}

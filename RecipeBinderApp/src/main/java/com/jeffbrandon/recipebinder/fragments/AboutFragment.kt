package com.jeffbrandon.recipebinder.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewbinding.AboutFragmentViewBinder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.fragment_about), AboutFragmentViewBinder.ViewContract {

    @Inject lateinit var binder: AboutFragmentViewBinder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.bind(view, this)
    }

    override fun send(intent: Intent) {
        startActivity(intent)
    }
}

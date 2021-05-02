package com.jeffbrandon.recipebinder.viewbinding

import android.content.Intent
import android.net.Uri
import android.view.View
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.FragmentAboutBinding
import javax.inject.Inject

class AboutFragmentViewBinder @Inject constructor() {

    interface ViewContract {
        fun send(intent: Intent)
    }

    fun bind(view: View, vc: ViewContract) {
        with(FragmentAboutBinding.bind(view)) {
            contactLink.setOnClickListener {
                openUri(view.context.getString(R.string.contact_uri), vc)
            }
            siteLink.setOnClickListener {
                openUri(view.context.getString(R.string.website_uri), vc)
            }
            supportLink.setOnClickListener {
                openUri(view.context.getString(R.string.support_uri), vc)
            }
        }
    }

    private fun openUri(uri: String, vc: ViewContract) {
        vc.send(Intent().apply {
            data = Uri.parse(uri)
            action = Intent.ACTION_VIEW
        })
    }
}

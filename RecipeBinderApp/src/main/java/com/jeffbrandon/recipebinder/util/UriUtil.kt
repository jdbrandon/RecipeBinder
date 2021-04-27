package com.jeffbrandon.recipebinder.util

import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Util for encapsulating dependency on static Uri methods for easy mocking.
 */
@Singleton
class UriUtil @Inject constructor() : UriHelper {

    override fun builder() = Uri.Builder()

    override fun parse(uri: String): Uri = Uri.parse(uri)
}

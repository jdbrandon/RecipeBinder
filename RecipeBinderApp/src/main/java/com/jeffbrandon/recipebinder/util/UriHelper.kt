package com.jeffbrandon.recipebinder.util

import android.net.Uri

interface UriHelper {
    fun builder(): Uri.Builder
    fun parse(uri: String): Uri
}

package com.jeffbrandon.recipebinder.viewbinding

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun LifecycleOwner.whileResumed(block: suspend () -> Unit): Job = lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.RESUMED) {
        block()
    }
}

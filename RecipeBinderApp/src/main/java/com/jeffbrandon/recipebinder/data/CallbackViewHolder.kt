package com.jeffbrandon.recipebinder.data

import android.view.View

/**
 * A view holder that sets an on click callback on a particular view for an item
 * @param T the type of data the [BindableViewHolder] shall bind
 * @param V the type of data that the callback shall handle
 * @param rootView the view for the [BindableViewHolder] to use
 * @param callbackView the view for which the callback should be performed
 * @param callback the callback implementation
 */
abstract class CallbackViewHolder<T, V>(rootView: View, callbackView: View, callback: (V) -> Unit) :
    BindableViewHolder<T>(rootView) {
    abstract val current: V

    init {
        callbackView.setOnClickListener { callback(current) }
    }
}

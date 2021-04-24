package com.jeffbrandon.recipebinder.data

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * An abstract bindable [RecyclerView.ViewHolder]
 * @param T the data type to bind
 * @param v the view for the ViewHolder
 */
abstract class BindableViewHolder<T>(v: View) : RecyclerView.ViewHolder(v) {
    /**
     * Performs data binding
     * @param item the data to bind
     */
    abstract fun bind(item: T)
}

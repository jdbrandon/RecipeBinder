package com.jeffbrandon.recipebinder.data

import androidx.recyclerview.widget.RecyclerView

/**
 * Abstract class implementing [RecyclerView.Adapter]
 * @param T the [BindableViewHolder] class for the [RecyclerView]
 * @param E specifies the type of data that shall be bound
 * @param data the backing list for the Recycler. Elements of type [E]
 */
abstract class ListRecyclerViewAdapter<T : BindableViewHolder<E>, E>(val data: List<E>) :
    RecyclerView.Adapter<T>() {
    override fun onBindViewHolder(holder: T, position: Int) = holder.bind(data[position])

    override fun getItemCount(): Int = data.size
}

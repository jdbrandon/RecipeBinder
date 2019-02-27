package com.jeffbrandon.recipebinder.data

import android.content.Context
import android.view.LayoutInflater
import android.widget.BaseAdapter

abstract class AppendableAdapter<T>(private val context: Context, private val dataSource: MutableList<T>) :
    BaseAdapter() {
    protected val inflater by lazy { context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater }

    override fun getItem(position: Int): T = dataSource[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = dataSource.size

    fun add(element: T) {
        dataSource.add(element)
        notifyDataSetChanged()
        notifyDataSetInvalidated()
    }
}
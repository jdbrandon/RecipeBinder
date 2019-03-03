package com.jeffbrandon.recipebinder.data

import android.content.Context
import android.view.LayoutInflater
import android.widget.BaseAdapter

abstract class AppendableAdapter<T>(private val context: Context, protected val dataSource: MutableList<T>) :
    BaseAdapter() {
    protected val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItem(position: Int): T = dataSource[position]

    override fun getItemId(position: Int): Long = dataSource[position].hashCode().toLong()

    override fun getCount(): Int = dataSource.size

    fun add(element: T) {
        dataSource.add(element)
        notifyDataSetChanged()
    }

    private fun add(i: Int, element: T) = dataSource.add(i, element)

    private fun remove(i: Int): T = dataSource.removeAt(i)

    fun getData(): List<T> = dataSource

    fun moveUp(position: Int) {
        if(position == 0) return
        val tmp = remove(position)
        add(position - 1, tmp)
        notifyDataSetChanged()
    }

    fun moveDown(position: Int) {
        if(position == dataSource.lastIndex) return
        val tmp = remove(position)
        add(position + 1, tmp)
        notifyDataSetChanged()
    }

    fun delete(position: Int) {
        remove(position)
        notifyDataSetChanged()
    }
}
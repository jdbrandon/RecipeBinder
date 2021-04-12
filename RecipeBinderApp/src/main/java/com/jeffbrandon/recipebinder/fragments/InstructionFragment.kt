package com.jeffbrandon.recipebinder.fragments

import androidx.recyclerview.widget.RecyclerView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.InstructionAdapter2
import com.jeffbrandon.recipebinder.data.InstructionViewHolder
import com.jeffbrandon.recipebinder.room.RecipeData

class InstructionFragment : ListFragment<InstructionViewHolder>() {
    override val nameResourceId: Int = R.string.instructions
    override fun buildAdapter(recipe: RecipeData): RecyclerView.Adapter<InstructionViewHolder> =
        InstructionAdapter2(recipe.instructions)
}

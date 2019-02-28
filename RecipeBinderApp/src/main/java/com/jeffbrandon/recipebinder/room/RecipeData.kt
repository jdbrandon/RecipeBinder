package com.jeffbrandon.recipebinder.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.enums.RecipeTag

@Entity
data class RecipeData(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var name: String,
    var cookTime: Int,
    var tags: MutableList<RecipeTag>,
    var ingredientsJson: List<Ingredient>,
    var instructionsJson: List<Instruction>
) {
    constructor() : this(null, "", 0, mutableListOf(), listOf(), listOf())
}

package com.jeffbrandon.recipebinder.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.enums.RecipeTag

@Entity
data class RecipeData(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    val name: String,
    val cookTime: Int,
    val tags: MutableList<RecipeTag>,
    @ColumnInfo(name = "ingredientsJson") val ingredients: List<Ingredient>,
    @ColumnInfo(name = "instructionsJson") val instructions: List<Instruction>,
) {
    constructor() : this(null, "", 0, mutableListOf(), listOf(), listOf())
}

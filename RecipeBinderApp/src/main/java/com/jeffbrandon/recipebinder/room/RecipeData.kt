package com.jeffbrandon.recipebinder.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class RecipeData(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="id") val recipeId: Long?,
    val name: String,
    val cookTime: Int,
    val tags: List<RecipeTag>,
    @ColumnInfo(name = "ingredientsJson") val ingredients: List<Ingredient>,
    @ColumnInfo(name = "instructionsJson") val instructions: List<Instruction>,
) {
    constructor() : this(null, "", 0, listOf(), listOf(), listOf())
}

package com.jeffbrandon.recipebinder.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeData(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var name: String,
    var cookTime: Int,
    var ingredientsJson: String,
    var instructionsJson: String
) {
    constructor() : this(null, "", 0, "", "")
}

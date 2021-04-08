package com.jeffbrandon.recipebinder.enums

enum class RecipeMode {
    EDIT,
    VIEW;

    companion object {
        fun fromOrdinal(i: Int) = when (i) {
            0 -> EDIT
            1 -> VIEW
            else -> VIEW
        }
    }
}

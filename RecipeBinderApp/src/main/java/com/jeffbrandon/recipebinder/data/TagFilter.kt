package com.jeffbrandon.recipebinder.data

import com.jeffbrandon.recipebinder.enums.RecipeTag

sealed class TagFilter {
    interface SetFilter {
        val tags: Set<RecipeTag>
    }

    object None : TagFilter()

    class Include private constructor(override val tags: Set<RecipeTag>) : TagFilter(), SetFilter {
        companion object {
            fun create(set: Set<RecipeTag>) = if (set.isNotEmpty()) Include(set) else None
        }
    }

    class Exclude private constructor(override val tags: Set<RecipeTag>) : TagFilter(), SetFilter {
        companion object {
            fun create(set: Set<RecipeTag>) = if (set.isNotEmpty()) Exclude(set) else None
        }
    }

    fun match(otherTags: Set<RecipeTag>): Boolean =
        when (this) {
            is Include -> otherTags.containsAll(tags)
            is Exclude -> !otherTags.containsAll(tags)
            is None -> true
        }
}

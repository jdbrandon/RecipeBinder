package com.jeffbrandon.recipebinder.data

import com.jeffbrandon.recipebinder.enums.RecipeTag

data class TagFilter(val tags: Set<RecipeTag>, val exclude: Boolean) {
    fun match(otherTags: Set<RecipeTag>): Boolean = if (tags.isNotEmpty()) {
        val contains = otherTags.containsAll(tags)
        if (exclude) !contains else contains
    } else true
}

package com.jeffbrandon.recipebinder.enums

enum class RecipeTag {
    INSTANT_POT,
    STOVE,
    OVEN,
    SOUS_VIDE,
    FAST,
    EASY,
    HEALTHY,
    VEGETARIAN,
    VEGAN;

    override fun toString(): String {
        return when(this) {
            INSTANT_POT -> "Instant Pot"
            STOVE -> "Stove Top"
            OVEN -> "Oven"
            SOUS_VIDE -> "Sous Vide"
            FAST -> "Fast"
            EASY -> "Easy"
            HEALTHY -> "Healthy"
            VEGETARIAN -> "Vegetarian"
            VEGAN -> "Vegan"
        }
    }
}

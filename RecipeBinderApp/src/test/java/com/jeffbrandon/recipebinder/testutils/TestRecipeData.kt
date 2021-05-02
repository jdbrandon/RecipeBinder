package com.jeffbrandon.recipebinder.testutils

import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.enums.UnitType
import com.jeffbrandon.recipebinder.room.RecipeData

@Suppress("MemberVisibilityCanBePrivate")
class TestRecipeData {
    companion object {
        fun buildTestData() = mutableListOf(
            RECIPE_1,
            RECIPE_2,
            RECIPE_3,
        )

        const val ID_1 = 1L
        const val NAME_1 = "testName"
        const val COOK_TIME_1 = 5
        const val SERVING_SIZE_1 = 4
        val TAGS_SET_1 = setOf(RecipeTag.EASY, RecipeTag.FAST, RecipeTag.HEALTHY)
        const val INGREDIENT_NAME_1_1 = "tomatoes"
        const val AMOUNT_1_1 = 600.1f
        val TYPE_1_1 = UnitType.NONE
        val INGREDIENT_1_1 = Ingredient(INGREDIENT_NAME_1_1, AMOUNT_1_1, TYPE_1_1)
        const val INGREDIENT_NAME_1_2 = "water"
        const val AMOUNT_1_2 = 5f
        val TYPE_1_2 = UnitType.CUP
        val INGREDIENT_1_2 = Ingredient(INGREDIENT_NAME_1_2, AMOUNT_1_2, TYPE_1_2)
        const val INGREDIENT_NAME_1_3 = "cheese"
        const val AMOUNT_1_3 = 1f
        val TYPE_1_3 = UnitType.POUND
        val INGREDIENT_1_3 = Ingredient(INGREDIENT_NAME_1_3, AMOUNT_1_3, TYPE_1_3)
        val INGREDIENT_LIST_1 = listOf(
            INGREDIENT_1_1,
            INGREDIENT_1_2,
            INGREDIENT_1_3,
        )
        val INSTRUCTION_1_1 = Instruction("make")
        val INSTRUCTION_1_2 = Instruction("the")
        val INSTRUCTION_1_3 = Instruction("pasta")
        val INSTRUCTION_LIST_1 = listOf(INSTRUCTION_1_1, INSTRUCTION_1_2, INSTRUCTION_1_3)
        const val ID_2 = 1L
        const val NAME_2 = "testName"
        const val COOK_TIME_2 = 5
        const val SERVING_SIZE_2 = 10
        val TAGS_SET_2 = setOf(RecipeTag.DESSERT)
        const val INGREDIENT_NAME_2_1 = "birds"
        const val AMOUNT_2_1 = 2f
        val TYPE_2_1 = UnitType.NONE
        val INGREDIENT_2_1 = Ingredient(INGREDIENT_NAME_2_1, AMOUNT_2_1, TYPE_2_1)
        const val INGREDIENT_NAME_2_2 = "eggs"
        const val AMOUNT_2_2 = 6f
        val TYPE_2_2 = UnitType.PINT
        val INGREDIENT_2_2 = Ingredient(INGREDIENT_NAME_2_2, AMOUNT_2_2, TYPE_2_2)
        const val INGREDIENT_NAME_2_3 = "fish"
        const val AMOUNT_2_3 = 0.2222222f
        val TYPE_2_3 = UnitType.GRAM
        val INGREDIENT_2_3 = Ingredient(INGREDIENT_NAME_2_3, AMOUNT_2_3, TYPE_2_3)
        val INGREDIENT_LIST_2 = listOf(
            INGREDIENT_2_1,
            INGREDIENT_2_2,
            INGREDIENT_2_3,
        )
        val INSTRUCTION_2_1 = Instruction("Now")
        val INSTRUCTION_2_2 = Instruction("we're")
        val INSTRUCTION_2_3 = Instruction("talking")
        val INSTRUCTION_LIST_2 = listOf(INSTRUCTION_2_1, INSTRUCTION_2_2, INSTRUCTION_2_3)
        const val ID_3 = 2L
        const val NAME_3 = "I am also a recipe"
        const val COOK_TIME_3 = 45000
        const val SERVING_SIZE_3 = 30
        val TAGS_SET_3 =
            setOf(RecipeTag.EASY, RecipeTag.DESSERT) // Chosen to intersect with recipe one and two distinctly
        const val INGREDIENT_NAME_3_1 = "impossible burger"
        const val AMOUNT_3_1 = 1f
        val TYPE_3_1 = UnitType.NONE
        val INGREDIENT_3_1 = Ingredient(INGREDIENT_NAME_3_1, AMOUNT_3_1, TYPE_3_1)
        const val INGREDIENT_NAME_3_2 = "lettuce"
        const val AMOUNT_3_2 = 2f
        val TYPE_3_2 = UnitType.NONE
        val INGREDIENT_3_2 = Ingredient(INGREDIENT_NAME_3_2, AMOUNT_3_2, TYPE_3_2)
        const val INGREDIENT_NAME_3_3 = "Buns"
        const val AMOUNT_3_3 = 2f
        val TYPE_3_3 = UnitType.NONE
        val INGREDIENT_3_3 = Ingredient(INGREDIENT_NAME_3_3, AMOUNT_3_3, TYPE_3_3)
        val INGREDIENT_LIST_3 = listOf(
            INGREDIENT_3_1,
            INGREDIENT_3_2,
            INGREDIENT_3_3,
        )
        val INSTRUCTION_3_1 = Instruction("Fry those illogical burgers up in a skillet")
        val INSTRUCTION_3_2 = Instruction("Put them on those tasty buns")
        val INSTRUCTION_3_3 = Instruction("lettuce goes on next")
        val INSTRUCTION_LIST_3 = listOf(INSTRUCTION_3_1, INSTRUCTION_3_2, INSTRUCTION_3_3)
        val RECIPE_1 = RecipeData(
            recipeId = ID_1,
            name = NAME_1,
            cookTime = COOK_TIME_1,
            servings = SERVING_SIZE_1,
            tags = TAGS_SET_1,
            ingredients = INGREDIENT_LIST_1,
            instructions = INSTRUCTION_LIST_1,
            image = null,
        )
        val RECIPE_2 = RecipeData(
            recipeId = ID_2,
            name = NAME_2,
            cookTime = COOK_TIME_2,
            servings = SERVING_SIZE_2,
            tags = TAGS_SET_2,
            ingredients = INGREDIENT_LIST_2,
            instructions = INSTRUCTION_LIST_2,
            image = null,
        )
        val RECIPE_3 = RecipeData(
            recipeId = ID_3,
            name = NAME_3,
            cookTime = COOK_TIME_3,
            servings = SERVING_SIZE_3,
            tags = TAGS_SET_3,
            ingredients = INGREDIENT_LIST_3,
            instructions = INSTRUCTION_LIST_3,
            image = null,
        )
    }
}

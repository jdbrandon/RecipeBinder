package com.jeffbrandon.recipebinder.testutils

import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.enums.UnitType
import com.jeffbrandon.recipebinder.room.RecipeData

class TestRecipeData {
    companion object {
        fun buildTestData() = mutableListOf(
            RECIPE_1,
            RECIPE_2,
        )

        const val ID_1 = 1L
        const val NAME_1 = "testName"
        const val COOK_TIME_1 = 5
        val TAGS_LIST_1 = listOf(RecipeTag.EASY, RecipeTag.FAST, RecipeTag.HEALTHY)
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
        val TAGS_LIST_2 = listOf(RecipeTag.DESSERT)
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
        val RECIPE_1 = RecipeData(id = ID_1,
                                  name = NAME_1,
                                  cookTime = COOK_TIME_1,
                                  tags = TAGS_LIST_1,
                                  ingredients = INGREDIENT_LIST_1,
                                  instructions = INSTRUCTION_LIST_1)
        val RECIPE_2 = RecipeData(id = ID_2,
                                  name = NAME_2,
                                  cookTime = COOK_TIME_2,
                                  tags = TAGS_LIST_2,
                                  ingredients = INGREDIENT_LIST_2,
                                  instructions = INSTRUCTION_LIST_2)
    }
}
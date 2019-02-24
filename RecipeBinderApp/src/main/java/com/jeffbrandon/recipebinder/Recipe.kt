package com.jeffbrandon.recipebinder

class Recipe(private val name: String) {
    var ingredients: MutableList<Ingredient> = mutableListOf()
    var instructions: MutableList<Instruction> = mutableListOf()
    var cookTime: Int? = null

    constructor(name: String, time: Int) : this(name) {
        cookTime = time
    }

    fun addIngredient(i: Ingredient) {
        ingredients.add(i)
    }

    fun addInstruction(i: Instruction) {
        instructions.add(i)
    }

    fun setCookTime(time: Int) {
        cookTime = time
    }
}
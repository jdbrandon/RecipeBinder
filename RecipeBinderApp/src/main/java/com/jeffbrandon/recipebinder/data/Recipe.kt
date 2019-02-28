package com.jeffbrandon.recipebinder.data

class Recipe(private val name: String) {

    private var ingredients: MutableList<Ingredient> = mutableListOf()
    private var instructions: MutableList<Instruction> = mutableListOf()
    private var tags: MutableList<String> = mutableListOf()
    private var cookTime: Int? = null

    constructor(name: String, time: Int) : this(name) {
        cookTime = time
    }

    fun getName(): String {
        return name
    }

    fun addIngredient(i: Ingredient) {
        ingredients.add(i)
    }

    fun getIngredientsList(): List<Ingredient> {
        return ingredients
    }

    fun addInstruction(i: Instruction) {
        instructions.add(i)
    }

    fun getInstructionsList(): List<Instruction> {
        return instructions
    }

    fun setCookTime(time: Int) {
        cookTime = time
    }

    fun getTags(): List<String> = tags
    fun addTag(s: String) = tags.add(s)
}
package com.jeffbrandon.recipebinder.moshi

import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class MoshiSingletons {
    companion object {
        private val instance by lazy {
            Moshi.Builder().build()
        }
        val ingredientConverter: JsonAdapter<List<Ingredient>> by lazy {
            instance.adapter(Types.newParameterizedType(List::class.java, Ingredient::class.java))
        }
        val instructionConverter: JsonAdapter<List<Instruction>> by lazy {
            instance.adapter(Types.newParameterizedType(List::class.java, Instruction::class.java))
        }
    }
}
package com.jeffbrandon.recipebinder.moshi

import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MoshiSingletons {
    companion object {
        private val instance by lazy {
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }
        val ingredientConverter: JsonAdapter<List<Ingredient>> by lazy {
            instance.adapter<List<Ingredient>>(Types.newParameterizedType(List::class.java,
                                                                          Ingredient::class.java))
        }
        val instructionConverter: JsonAdapter<List<Instruction>> by lazy {
            instance.adapter<List<Instruction>>(Types.newParameterizedType(List::class.java,
                                                                           Instruction::class.java))
        }
    }
}
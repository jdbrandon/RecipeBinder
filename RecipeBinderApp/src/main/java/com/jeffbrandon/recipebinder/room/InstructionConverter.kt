package com.jeffbrandon.recipebinder.room

import androidx.room.TypeConverter
import com.jeffbrandon.recipebinder.data.Instruction
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class InstructionConverter {

    companion object {
        @TypeConverter
        @JvmStatic
        fun toString(instructions: List<Instruction>): String {
            return when(instructions.size) {
                0 -> ""
                else -> getJsonAdapter().toJson(instructions)
            }
        }

        @TypeConverter
        @JvmStatic
        fun toListInstruction(json: String): List<Instruction> {
            json.run {
                if(isEmpty()) return listOf()
                return getJsonAdapter().fromJson(this)!!
            }
        }

        @JvmStatic
        private fun getJsonAdapter(): JsonAdapter<List<Instruction>> {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            return moshi.adapter<List<Instruction>>(Types.newParameterizedType(List::class.java,
                                                                               Instruction::class.java))
        }
    }
}
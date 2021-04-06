package com.jeffbrandon.recipebinder.room

import androidx.room.TypeConverter
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.moshi.MoshiSingletons

class InstructionConverter private constructor() {

    companion object {
        @TypeConverter
        @JvmStatic
        fun toString(instructions: List<Instruction>): String {
            return when (instructions.size) {
                0 -> ""
                else -> MoshiSingletons.instructionConverter.toJson(instructions)
            }
        }

        @TypeConverter
        @JvmStatic
        fun toListInstruction(json: String): List<Instruction> {
            json.run {
                if (isEmpty()) return listOf()
                return MoshiSingletons.instructionConverter.fromJson(this)!!
            }
        }
    }
}

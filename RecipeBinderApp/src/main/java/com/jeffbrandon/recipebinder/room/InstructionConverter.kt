package com.jeffbrandon.recipebinder.room

import androidx.room.TypeConverter
import com.jeffbrandon.recipebinder.dagger.MoshiModule
import com.jeffbrandon.recipebinder.data.Instruction

class InstructionConverter private constructor() {

    companion object {
        @TypeConverter
        @JvmStatic
        fun toString(instructions: List<Instruction>): String {
            return when (instructions.size) {
                0 -> ""
                else -> MoshiModule.instructionConverter.toJson(instructions)
            }
        }

        @TypeConverter
        @JvmStatic
        fun toListInstruction(json: String): List<Instruction> {
            json.run {
                if (isEmpty()) return listOf()
                return MoshiModule.instructionConverter.fromJson(this)
                    ?: error("failed to parse instructions")
            }
        }
    }
}

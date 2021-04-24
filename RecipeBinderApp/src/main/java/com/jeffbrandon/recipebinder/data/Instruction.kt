package com.jeffbrandon.recipebinder.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Instruction(val text: String)

package com.jeffbrandon.recipebinder

class Instruction(private val ingredients: List<Ingredient>, private val text: String) {

    fun get(): String {
        val sb = StringBuilder()
        sb.apply {
            for(i in ingredients) {
                append("${i.name}, ")
            }
            setCharAt(lastIndex - 1, ':')
            append(text)
        }
        return sb.toString()
    }
}

package com.jeffbrandon.recipebinder.util

interface Base64Encoder {
    fun encodeToString(bytes: ByteArray, offset: Int, maxToEncode: Int, flags: Int): String
    fun decode(input: String, flags: Int): ByteArray
}

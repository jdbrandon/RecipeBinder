package com.jeffbrandon.recipebinder.util

interface CompressionHelper {
    @Throws(IllegalStateException::class)
    fun inflate(bytes: ByteArray): String

    @Throws(IllegalStateException::class)
    fun deflateInto(input: String, out: ByteArray): Int
}

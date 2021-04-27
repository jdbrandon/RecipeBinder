package com.jeffbrandon.recipebinder.util

import com.jeffbrandon.recipebinder.util.RecipeImportExportUtil.Companion.BUFFER_SIZE
import java.util.zip.Deflater
import java.util.zip.Inflater
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class to encapsulate the compression dependency for easy mocking in tests
 */
@Singleton
class CompressionUtil @Inject constructor() : CompressionHelper {

    @Throws(IllegalStateException::class)
    override fun inflate(bytes: ByteArray): String = with(Inflater()) {
        var error: Throwable? = null
        setInput(bytes, 0, bytes.size)
        val result = ByteArray(BUFFER_SIZE)
        val length = inflate(result)
        if (length == BUFFER_SIZE && !finished()) {
            error = IllegalStateException("Input bytes are too large")
        }
        end()
        error?.let { throw error }
        return String(result, 0, length, Charsets.UTF_8)
    }

    @Throws(IllegalStateException::class)
    override fun deflateInto(input: String, out: ByteArray): Int = Deflater().let {
        var error: Throwable? = null
        it.setInput(input.toByteArray(Charsets.UTF_8))
        it.finish()
        val length = it.deflate(out, 0, BUFFER_SIZE)
        if (length == BUFFER_SIZE && !it.finished()) {
            error = IllegalStateException("Compressed recipe size was too big")
        }
        it.end()
        error?.let { throw error }
        return length
    }
}

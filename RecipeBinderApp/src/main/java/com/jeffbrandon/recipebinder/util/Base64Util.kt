package com.jeffbrandon.recipebinder.util

import android.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Encapsulates Base64 Dependency for easy mocking in tests
 */
@Singleton
class Base64Util @Inject constructor() : Base64Encoder {
    override fun encodeToString(
        bytes: ByteArray,
        offset: Int,
        maxToEncode: Int,
        flags: Int,
    ): String = Base64.encodeToString(bytes, offset, maxToEncode, flags)

    override fun decode(input: String, flags: Int): ByteArray = Base64.decode(input, flags)
}

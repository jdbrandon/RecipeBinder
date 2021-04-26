package com.jeffbrandon.recipebinder.util

import android.content.Context
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.testutils.MainCoroutineRule
import com.jeffbrandon.recipebinder.testutils.TestRecipeData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class RecipeImportExportUtilTest {

    private companion object {
        private const val SCHEME = "testScheme"
        private const val PATH = "testPath"
        private const val ERROR = "error"
        private const val TEST_B64_BLOB = "don't mind me I'm a blob"
        private const val JSON = "testJsonVal"
        private const val COMPRESSED_SIZE = 500
    }

    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule val coroutineRule = MainCoroutineRule()

    @Mock private lateinit var context: Context
    @Mock private lateinit var uriUtil: UriHelper
    @Mock private lateinit var uri: Uri
    @Mock private lateinit var uriBuilder: Uri.Builder
    @Mock private lateinit var base64: Base64Encoder
    @Mock private lateinit var compression: CompressionHelper

    private lateinit var json: JsonAdapter<RecipeData>
    private lateinit var underTest: RecipeImportExportUtil

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // For encoding
        whenever(context.getString(R.string.app_scheme)).thenReturn(SCHEME)
        whenever(context.getString(R.string.recipe_uri_path)).thenReturn(PATH)
        whenever(context.getString(R.string.error_recipe_too_large)).thenReturn(ERROR)

        whenever(compression.deflateInto(anyString(), any())).thenReturn(COMPRESSED_SIZE)

        whenever(base64.encodeToString(any(), eq(0), anyInt(), anyInt())).thenReturn(TEST_B64_BLOB)

        whenever(uriUtil.builder()).thenReturn(uriBuilder)

        whenever(uriBuilder.scheme(any())).thenReturn(uriBuilder)
        whenever(uriBuilder.path(any())).thenReturn(uriBuilder)
        whenever(uriBuilder.encodedFragment(any())).thenReturn(uriBuilder)
        whenever(uriBuilder.build()).thenReturn(uri)

        whenever(uri.toString()).thenReturn("$SCHEME:$PATH#$TEST_B64_BLOB")

        // For decoding
        whenever(uriUtil.parse(any())).thenReturn(uri)
        whenever(uri.scheme).thenReturn(SCHEME)
        // Android Uri util adds a slash like so
        whenever(uri.path).thenReturn("/$PATH")
        whenever(uri.encodedFragment).thenReturn(TEST_B64_BLOB)

        whenever(base64.decode(any(), anyInt())).thenReturn(JSON.toByteArray())

        json = Moshi.Builder().build().adapter(RecipeData::class.java)

        whenever(compression.inflate(any())).thenReturn(json.toJson(TestRecipeData.RECIPE_1))

        underTest = RecipeImportExportUtil(context, { json }, { base64 }, { uriUtil }, { compression })
    }

    @Test
    fun `test encode valid`() = runBlocking {
        val uriStr = underTest.encode(TestRecipeData.RECIPE_1)

        assertNotNull(uriStr)
        val uri = uriStr.split(':')
        assertEquals(SCHEME, uri[0])
        val pathAndFragment = uri[1].split('#')
        assertEquals(PATH, pathAndFragment[0])
        assertEquals(TEST_B64_BLOB, pathAndFragment[1])
    }

    @Test
    fun `test encode too big`() = runBlocking {
        whenever(compression.deflateInto(anyString(), any())).thenThrow(IllegalStateException("test exception"))

        val uriStr = underTest.encode(TestRecipeData.RECIPE_1)

        assertEquals(ERROR, uriStr)
    }

    @Test
    fun `test import valid`() = runBlocking {
        val data = underTest.import("dummyBlob")

        // must ensure id is null
        assertEquals(TestRecipeData.RECIPE_1.copy(recipeId = null), data)
    }

    @Test
    fun `test import too big`() = runBlocking {
        whenever(compression.inflate(any())).thenThrow(IllegalStateException("test exception"))

        val data = underTest.import("dummyBlob")

        assertEquals(null, data)
    }

    @Test
    fun `test import malformed json`() = runBlocking {
        whenever(compression.inflate(any())).thenReturn("this is definitely not json")

        val data = underTest.import("dummyBlob")

        assertEquals(null, data)
    }

    @Test
    fun `test import malformed base64`() = runBlocking {
        whenever(base64.decode(anyString(), anyInt())).thenThrow(IllegalArgumentException())

        val data = underTest.import("dummyBlob")

        assertEquals(null, data)
    }
}

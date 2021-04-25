package com.jeffbrandon.recipebinder.util

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.util.zip.Deflater
import java.util.zip.Inflater
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeImportExportUtil @Inject constructor(
    @ApplicationContext context: Context,
    lazyAdapter: Lazy<JsonAdapter<RecipeData>>,
) : RecipeBlobImporter, RecipeExporter {

    private companion object {
        // (1 shl 10) Should be enough to hold roughly half a page of uncompressed text
        private const val BUFFER_SIZE = 5 * (1 shl 10)
    }

    private val json by lazy { lazyAdapter.get() }
    private val uriScheme by lazy { context.getString(R.string.app_scheme) }
    private val recipeUriPath by lazy { context.getString(R.string.recipe_uri_path) }
    private val errorTooBig by lazy { context.getString(R.string.error_recipe_too_large) }

    override suspend fun encode(recipe: RecipeData): String = withContext(Dispatchers.Default) {
        val clearIdRecipe = recipe.copy(id = null)
        val buffer = ByteArray(BUFFER_SIZE)
        try {
            val compressedSize = json.toJson(clearIdRecipe).deflateInto(buffer)
            val encodedRecipe = Base64.encodeToString(buffer, 0, compressedSize, Base64.URL_SAFE)
            Uri.Builder().scheme(uriScheme).path(recipeUriPath).encodedFragment(encodedRecipe)
                .build().toString()
        } catch (e: IllegalStateException) {
            Timber.w(e, "failed to encode ${recipe.name}")
            errorTooBig
        }
    }

    /**
     * Try to import based on the app export format, input sanitization is crucial as this is the
     * biggest entry point into the app
     * @param blob string data to parse into recipe
     * @return [RecipeData] on successful import, null on failure
     * @see [encode]
     */
    override suspend fun import(blob: String): RecipeData? = withContext(Dispatchers.IO) {
        val uri = Uri.parse(blob)
        if (!validate(uri)) {
            Timber.w("Invalid uri")
            return@withContext null
        }
        // try to parse the fragment
        uri.fragment?.let { b64 ->
            val bytes = Base64.decode(b64, Base64.URL_SAFE)
            // Suppressing because we are using Dispatchers.IO
            @Suppress("BlockingMethodInNonBlockingContext") try {
                val maybeJson = inflate(bytes)
                return@withContext json.fromJson(maybeJson)
            } catch (e: IllegalStateException) {
                Timber.w(e, "blob was too big to import")
            } catch (e: JsonDataException) {
                Timber.w(e, "Tried to import from badly formed data")
            } catch (e: IOException) {
                Timber.w(e, "IO error encountered while trying to import recipe")
            } catch (e: Exception) {
                Timber.e(e, "Unhandled exception when importing")
            }
        }
        null
    }

    private fun validate(uri: Uri): Boolean {
        Timber.i(uri.toString())
        Timber.i("authority ${uri.authority}")
        if (uri.scheme != uriScheme) {
            Timber.w("scheme doesn't match app scheme")
            return false
        }
        if (uri.path?.trim('/') != recipeUriPath) {
            Timber.w("path ${uri.path} doesn't match expected recipe path")
            return false
        }
        return (uri.encodedFragment != null).also { if (!it) Timber.w("uri fragment was null") }
    }

    @Throws(IllegalStateException::class)
    private fun inflate(bytes: ByteArray): String = with(Inflater()) {
        var error: Throwable? = null
        setInput(bytes, 0, bytes.size)
        val result = ByteArray(BUFFER_SIZE)
        val length = inflate(result)
        if (length == BUFFER_SIZE && inflate(ByteArray(1), BUFFER_SIZE, 1) > 0) {
            error = IllegalStateException("Input bytes are too large")
        }
        end()
        error?.let { throw error }
        return String(result, 0, length, Charsets.UTF_8)
    }

    @Throws(IllegalStateException::class)
    private fun String.deflateInto(out: ByteArray): Int = Deflater().let {
        var error: Throwable? = null
        it.setInput(toByteArray(Charsets.UTF_8))
        it.finish()
        val length = it.deflate(out, 0, BUFFER_SIZE)
        if (length == BUFFER_SIZE && it.deflate(ByteArray(1), BUFFER_SIZE, 1) > 0) {
            error = IllegalStateException("Compressed recipe size was too big")
        }
        it.end()
        error?.let { throw error }
        return length
    }
}

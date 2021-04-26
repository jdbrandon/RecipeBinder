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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeImportExportUtil @Inject constructor(
    @ApplicationContext context: Context,
    lazyAdapter: Lazy<JsonAdapter<RecipeData>>,
    lazyBase64: Lazy<Base64Encoder>,
    lazyUri: Lazy<UriHelper>,
    lazyCompressor: Lazy<CompressionHelper>,
) : RecipeBlobImporter, RecipeExporter {

    companion object {
        // (1 shl 10) Should be enough to hold roughly half a page of uncompressed text
        const val BUFFER_SIZE = 5 * (1 shl 10)
    }

    private val json by lazy { lazyAdapter.get() }
    private val base64 by lazy { lazyBase64.get() }
    private val uri by lazy { lazyUri.get() }
    private val compressor by lazy { lazyCompressor.get() }
    private val uriScheme by lazy { context.getString(R.string.app_scheme) }
    private val recipeUriPath by lazy { context.getString(R.string.recipe_uri_path) }
    private val errorTooBig by lazy { context.getString(R.string.error_recipe_too_large) }

    override suspend fun encode(recipe: RecipeData): String = withContext(Dispatchers.Default) {
        val clearIdRecipe = recipe.copy(id = null)
        val buffer = ByteArray(BUFFER_SIZE)
        try {
            with(compressor) {
                val json = json.toJson(clearIdRecipe)
                val compressedSize = compressor.deflateInto(json, buffer)
                val encodedRecipe = base64.encodeToString(buffer, 0, compressedSize, Base64.URL_SAFE)
                uri.builder().scheme(uriScheme).path(recipeUriPath).encodedFragment(encodedRecipe).build().toString()
            }
        } catch (e: IllegalStateException) {
            Timber.w(e, "failed to encode ${recipe.name}")
            errorTooBig
        }
    }

    /**
     * Try to import based on the app export format, input sanitizing is crucial as this is the
     * biggest entry point into the app.
     *
     * Make sure the id is null otherwise there may be a collision in the local database. This way
     * the database can assign it's own local unique id. When exporting we don't send any id info
     * but this protects against someone crafting a payload that might crash the app.
     * @param blob string data to parse into recipe
     * @return [RecipeData] on successful import, null on failure
     * @see [encode]
     */
    override suspend fun import(blob: String): RecipeData? = withContext(Dispatchers.IO) {
        val uri = uri.parse(blob)
        if (!validate(uri)) {
            Timber.w("Invalid uri")
            return@withContext null
        }
        // try to parse the fragment
        uri.encodedFragment?.let { b64 ->
            // Suppressing because we are using Dispatchers.IO
            @Suppress("BlockingMethodInNonBlockingContext", "TooGenericExceptionCaught") try {
                val bytes = base64.decode(b64, Base64.URL_SAFE)
                val maybeJson = compressor.inflate(bytes)
                return@withContext json.fromJson(maybeJson)?.copy(id = null)
            } catch (e: IllegalArgumentException) {
                Timber.w(e, "Bad arguments, was your base64 well formed?")
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
        var failed = false
        if (uri.scheme != uriScheme) {
            Timber.w("scheme doesn't match app scheme")
            failed = true
        }
        if (!failed && uri.path?.trim('/') != recipeUriPath) {
            Timber.w("path ${uri.path} doesn't match expected recipe path")
            failed = true
        }
        return !failed && (uri.encodedFragment != null).also { if (!it) Timber.w("uri fragment was null") }
    }
}

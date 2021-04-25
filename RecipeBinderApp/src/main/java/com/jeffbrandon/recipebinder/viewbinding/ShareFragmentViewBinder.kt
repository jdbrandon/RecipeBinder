package com.jeffbrandon.recipebinder.viewbinding

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Base64
import android.view.View
import androidx.core.graphics.set
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.databinding.FragmentShareRecipeBinding
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel
import com.squareup.moshi.JsonAdapter
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.zip.Deflater
import javax.inject.Inject
import kotlin.math.roundToInt

class ShareFragmentViewBinder @Inject constructor(
    @ApplicationContext private val context: Context,
    lazyAdapter: Lazy<JsonAdapter<RecipeData>>,
) {

    private val json by lazy { lazyAdapter.get() }
    private lateinit var binder: FragmentShareRecipeBinding
    private val clipManager: ClipboardManager by lazy { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    private val qrSize by lazy { context.resources.getDimension(R.dimen.qr_size).roundToInt() }
    private val uriScheme by lazy { context.getString(R.string.app_scheme) }
    private val titleJoin by lazy { context.getString(R.string.title_join_format) }
    private val joinFmt by lazy { context.getString(R.string.list_join_format) }
    private val instructionString by lazy { context.getString(R.string.instructions) }
    private val ingredientString by lazy { context.getString(R.string.ingredients) }

    fun bind(vm: RecipeViewModel, viewRoot: View, lifecycleOwner: LifecycleOwner) {
        binder = FragmentShareRecipeBinding.bind(viewRoot)
        vm.getRecipe().observe(lifecycleOwner) { recipe ->
            vm.viewModelScope.launch {
                val uriString = encode(recipe)
                launch {
                    binder.copyUriButton.setOnClickListener {
                        addToClipboard(recipe.name, Uri.parse(uriString), viewRoot)
                    }
                }
                launch {
                    binder.copyRawButton.setOnClickListener {
                        addToClipboard(recipe.name, getClipString(recipe), viewRoot)
                    }
                }
                val bitmap = uriString.asQRCode()
                binder.qrCode.setImageBitmap(bitmap)
            }
        }
    }

    private fun getClipString(recipe: RecipeData): String {
        with(recipe) {
            val formattedIngredients = ingredientsString(ingredients)
            val formattedInstructions = instructionsString(instructions)
            return context.getString(R.string.recipe_string_format,
                                     name,
                                     cookTime,
                                     formattedIngredients,
                                     formattedInstructions)
        }
    }

    private fun instructionsString(instructions: List<Instruction>): String {
        val list = instructions.joinToString(joinFmt) { ins -> ins.text }
        return "$instructionString$titleJoin${list}"
    }

    private fun ingredientsString(ingredients: List<Ingredient>): String {
        val list = ingredients.joinToString(joinFmt) { ing ->
            context.getString(R.string.ingredient_format, ing.amountString(context), ing.name)
        }
        return "$ingredientString$titleJoin${list}"
    }

    private suspend fun encode(recipe: RecipeData): String = withContext(Dispatchers.Default) {
        val buffer = ByteArray(1000)
        val compressedSize = json.toJson(recipe).deflateInto(buffer)
        val encodedRecipe = Base64.encodeToString(buffer, 0, compressedSize, Base64.URL_SAFE)
        "$uriScheme://$encodedRecipe"
    }

    private fun String.deflateInto(out: ByteArray): Int = Deflater().let {
        it.setInput(toByteArray(Charsets.UTF_8))
        it.finish()
        val length = it.deflate(out)
        it.end()
        return length
    }

    private fun String.asQRCode(): Bitmap {
        val matrix = QRCodeWriter().encode(this, BarcodeFormat.QR_CODE, qrSize, qrSize)

        val bm = Bitmap.createBitmap(matrix.width, matrix.height, Bitmap.Config.RGB_565)
        for (i in 0 until matrix.width) {
            for (j in 0 until matrix.height) {
                bm[i, j] = if (matrix[i, j]) Color.BLACK else Color.WHITE
            }
        }
        return bm
    }

    private fun addToClipboard(label: String, content: Uri, view: View) {
        val clip = ClipData.newRawUri(label, content)
        setAndAnnounce(clip, view)
    }

    private fun addToClipboard(label: String, content: String, view: View) {
        val clip = ClipData.newPlainText(label, content)
        setAndAnnounce(clip, view)
    }

    private fun setAndAnnounce(clip: ClipData, view: View) {
        clipManager.setPrimaryClip(clip)
        Snackbar.make(view, R.string.copied_toast, Snackbar.LENGTH_SHORT).show()
    }
}

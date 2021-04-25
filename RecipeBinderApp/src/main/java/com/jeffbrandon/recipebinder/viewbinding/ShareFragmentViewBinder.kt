package com.jeffbrandon.recipebinder.viewbinding

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Base64
import android.view.View
import androidx.core.graphics.set
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.jeffbrandon.recipebinder.R
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
    @ApplicationContext context: Context,
    lazyAdapter: Lazy<JsonAdapter<RecipeData>>,
) {

    private val json by lazy { lazyAdapter.get() }
    private lateinit var binder: FragmentShareRecipeBinding
    private val qrSize by lazy { context.resources.getDimension(R.dimen.qr_size).roundToInt() }
    private val uriScheme by lazy { context.getString(R.string.app_scheme) }

    fun bind(vm: RecipeViewModel, viewRoot: View, lifecycleOwner: LifecycleOwner) {
        binder = FragmentShareRecipeBinding.bind(viewRoot)
        vm.getRecipe().observe(lifecycleOwner) {
            vm.viewModelScope.launch {
                val b64String = encode(it)
                val bitmap = b64String.asQRCode()
                binder.qrCode.setImageBitmap(bitmap)
            }
        }
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
}

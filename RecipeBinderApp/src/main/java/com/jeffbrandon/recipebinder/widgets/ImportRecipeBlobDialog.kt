package com.jeffbrandon.recipebinder.widgets

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import com.google.android.material.textfield.TextInputEditText
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportRecipeBlobDialog @Inject constructor() {
    private lateinit var viewModel: RecipeMenuViewModel
    private lateinit var textBox: TextInputEditText
    private fun inflateView(context: Context) =
        View.inflate(context, R.layout.dialog_import_blob, null).also {
            textBox = ViewCompat.requireViewById(it, R.id.blob)
            textBox.requestFocus()
        }

    fun show(context: Context, vm: RecipeMenuViewModel) {
        viewModel = vm
        AlertDialog.Builder(context).setView(inflateView(context))
            .setPositiveButton(R.string.menu_import) { _, _ ->
                viewModel.import(textBox.text.toString().trim())
            }.setNeutralButton(android.R.string.cancel) { _, _ ->
                textBox.text?.clear()
            }.setCancelable(true).show()
    }
}

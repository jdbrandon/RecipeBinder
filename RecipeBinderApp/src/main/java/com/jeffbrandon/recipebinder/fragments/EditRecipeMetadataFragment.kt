package com.jeffbrandon.recipebinder.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewbinding.EditRecipeMetadataViewBinder
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditRecipeMetadataFragment : EditFragment(R.layout.fragment_edit_recipe_metadata),
                                   EditRecipeMetadataViewBinder.ViewContract {
    private lateinit var activityLauncher: ActivityResultLauncher<Uri>
    @Inject lateinit var binder: EditRecipeMetadataViewBinder
    private val viewModel: EditRecipeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            activityLauncher
            binder.saveBitmap(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.bind(viewModel, this, requireView(), viewLifecycleOwner)
    }

    override fun edit() {
        binder.edit()
    }

    override suspend fun save() {
        binder.save()
    }

    override fun startCameraActivityForResult(uri: Uri) {
        activityLauncher.launch(uri)
    }
}

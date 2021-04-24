package com.jeffbrandon.recipebinder.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.FragmentListBinding
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel

abstract class ListFragment<T : RecyclerView.ViewHolder> : Fragment(R.layout.fragment_list) {
    private val viewModel: RecipeViewModel by activityViewModels()

    abstract val nameResourceId: Int

    abstract fun buildAdapter(recipe: RecipeData): RecyclerView.Adapter<T>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentListBinding.bind(view)) {
            name.text = view.context.getString(nameResourceId)
            viewModel.getRecipe().observe(viewLifecycleOwner) { recipeData ->
                recycler.adapter = buildAdapter(recipeData)
            }
        }
    }
}

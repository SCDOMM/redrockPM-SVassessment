package com.example.ept.dicover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.category.CategoryDetailFragment

class DiscoveryFragment : Fragment() {

    private val viewModel: DiscoveryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvCategory = view.findViewById<RecyclerView>(R.id.rv_doscovery_category)
        rvCategory.layoutManager = GridLayoutManager(requireContext(), 3)

        val adapter = CategoryAdapter { category ->
            val fragment = CategoryDetailFragment.newInstance(category.apiUrl)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        rvCategory.adapter = adapter

        viewModel.categories.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.loadCategories()
    }

    companion object {
        @JvmStatic
        fun newInstance() = DiscoveryFragment()
    }
}

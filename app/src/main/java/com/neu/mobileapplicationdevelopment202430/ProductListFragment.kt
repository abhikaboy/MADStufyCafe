package com.neu.mobileapplicationdevelopment202430

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.neu.mobileapplicationdevelopment202430.databinding.FragmentProductListBinding
import com.neu.mobileapplicationdevelopment202430.viewmodels.ProductViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSpinner()
        setupObservers()
        
        viewModel.loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter()
        binding.recyclerView.apply {
            adapter = this@ProductListFragment.adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupSpinner() {
        val categories = arrayOf("All", "Food", "Equipment")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        binding.categorySpinner.adapter = spinnerAdapter
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val category = if (position == 0) null else categories[position].lowercase()
                viewModel.setCategory(category)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.loadingIndicator.isVisible = isLoading
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                binding.errorText.isVisible = error != null
                if (error != null) {
                    binding.errorText.text = error
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collectLatest { products ->
                binding.errorText.isVisible = products.isEmpty()
                binding.errorText.text = if (products.isEmpty()) "No products available" else ""
                adapter.submitList(products)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
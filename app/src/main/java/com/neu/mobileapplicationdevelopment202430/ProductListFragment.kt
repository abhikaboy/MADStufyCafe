package com.neu.mobileapplicationdevelopment202430

import android.os.Bundle
import android.util.Log
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

private const val TAG = "ProductListFragment"

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
        Log.d(TAG, "onCreateView called")
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")

        setupRecyclerView()
        setupSpinner()
        setupObservers()
        
        Log.d(TAG, "Initiating product load")
        viewModel.loadProducts()
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView")
        adapter = ProductAdapter()
        binding.recyclerView.apply {
            adapter = this@ProductListFragment.adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupSpinner() {
        Log.d(TAG, "Setting up Spinner")
        val sortOptions = arrayOf("Sort A-Z", "Sort Z-A")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        binding.sortSpinner.adapter = spinnerAdapter
        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val isAscending = position == 0
                Log.d(TAG, "Spinner selection changed: position=$position, isAscending=$isAscending")
                viewModel.setSortOrder(isAscending)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "Nothing selected in spinner")
            }
        }
    }

    private fun setupObservers() {
        Log.d(TAG, "Setting up observers")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                Log.d(TAG, "Loading state changed: $isLoading")
                binding.loadingIndicator.isVisible = isLoading
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                Log.d(TAG, "Error state changed: $error")
                binding.errorText.isVisible = error != null
                if (error != null) {
                    binding.errorText.text = error
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collectLatest { products ->
                Log.d(TAG, "Products updated: ${products.size} items")
                products.forEach { product ->
                    Log.d(TAG, "Product in UI: ${product.name}")
                }
                binding.errorText.isVisible = products.isEmpty()
                binding.errorText.text = if (products.isEmpty()) "No products available" else ""
                adapter.submitList(products)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView called")
        _binding = null
    }
} 
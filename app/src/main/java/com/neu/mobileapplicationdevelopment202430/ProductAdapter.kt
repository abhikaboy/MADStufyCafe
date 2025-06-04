package com.neu.mobileapplicationdevelopment202430

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.neu.mobileapplicationdevelopment202430.databinding.ItemProductBinding
import com.neu.mobileapplicationdevelopment202430.model.Product

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                productImage.setImageResource(product.getImageResource())

                productName.setTextColor(Color.parseColor("#000000"))
                productPrice.setTextColor(Color.parseColor("#000000"))
                expiryDateValue.setTextColor(Color.parseColor("#000000"))
                warrantyValue.setTextColor(Color.parseColor("#000000"))

                productName.text = product.name
                productPrice.text = "$${product.price}"

                when (product) {
                    is Product.Food -> {
                        expiryDateValue.isVisible = true
                        warrantyValue.isVisible = false
                        expiryDateValue.text = "Expires: ${product.expiryDate ?: "N/A"}"
                        infoContainer.setBackgroundColor(Color.parseColor("#6D7F7A"))
                    }
                    is Product.Equipment -> {
                        expiryDateValue.isVisible = false
                        warrantyValue.isVisible = true
                        warrantyValue.text = "Warranty: ${product.warranty ?: "N/A"} years"
                        infoContainer.setBackgroundColor(Color.parseColor("#016A7B"))
                    }
                }
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
} 
package com.neu.mobileapplicationdevelopment202430

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.neu.mobileapplicationdevelopment202430.databinding.ItemProductBinding

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

                val backgroundColor = if (product.isEquipment()) "#016A7B" else "#6D7F7A"
                infoContainer.setBackgroundColor(Color.parseColor(backgroundColor))

                if (product.isFood() && !product.expiryDate.isNullOrBlank() && product.expiryDate != "null") {
                    expiryDateValue.isVisible = true
                    expiryDateValue.text = product.expiryDate
                } else {
                    expiryDateValue.isVisible = false
                }

                if (product.isEquipment() && !product.warranty.isNullOrBlank() && product.warranty != "null") {
                    warrantyValue.isVisible = true
                    warrantyValue.text = "${product.warranty} years"
                } else {
                    warrantyValue.isVisible = false
                }
            }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
} 
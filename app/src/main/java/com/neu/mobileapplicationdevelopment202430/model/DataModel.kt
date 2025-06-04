package com.neu.mobileapplicationdevelopment202430.model

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("products") val products: List<Product>
)


package com.neu.mobileapplicationdevelopment202430.api

import com.google.gson.*
import com.neu.mobileapplicationdevelopment202430.model.Product
import java.lang.reflect.Type

class ProductTypeAdapter : JsonDeserializer<Product> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Product {
        val jsonObject = json.asJsonObject
        val category = jsonObject.get("category").asString.lowercase()
        
        return Product.fromApiResponse(
            id = jsonObject.get("id").asString,
            name = jsonObject.get("name").asString,
            category = category,
            expiryDate = jsonObject.get("expiryDate")?.asString,
            price = jsonObject.get("price").asString,
            warranty = jsonObject.get("warranty")?.asString,
            imageUrl = jsonObject.get("imageUrl").asString
        )
    }
} 
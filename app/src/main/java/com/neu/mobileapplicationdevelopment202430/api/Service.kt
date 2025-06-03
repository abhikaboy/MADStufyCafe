interface Service {
    @GET("products")
    suspend fun getProducts(): List<Product>
}
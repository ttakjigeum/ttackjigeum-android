package com.devhjs.ttackjigeum_android.domain.repository

import com.devhjs.ttackjigeum_android.domain.model.Product


interface ProductRepository {
    suspend fun getProducts(): List<Product>
    suspend fun getProductById(id: Long): Product?
    suspend fun addProduct(product: Product)
    suspend fun deleteProduct(id: Long)
    suspend fun deleteProduct(product: Product)
}

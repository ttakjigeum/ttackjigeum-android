package com.devhjs.ttackjigeum_android.data.repository.mock

import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository


class MockProductRepositoryImpl : ProductRepository {
    override suspend fun getProducts(): List<Product> {
        return MockData.MockProducts
    }

    override suspend fun getProductById(id: Long): Product? {
        return MockData.MockProducts.find { it.id == id }
    }

    override suspend fun addProduct(product: Product) {
        // Mock implementation
    }

    override suspend fun deleteProduct(id: Long) {
        // Mock implementation
    }

    override suspend fun deleteProduct(product: Product) {
        // Mock implementation
    }
}
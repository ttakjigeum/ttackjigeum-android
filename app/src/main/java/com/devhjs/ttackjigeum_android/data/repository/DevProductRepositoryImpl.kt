package com.devhjs.ttackjigeum_android.data.repository

import com.devhjs.ttackjigeum_android.data.repository.mock.MockData
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository

class DevProductRepositoryImpl : ProductRepository {
    override suspend fun getProducts(): List<Product> {
        return MockData.MockProducts
    }

    override suspend fun getProductById(id: Long): Product? {
        return MockData.MockProducts.find { it.id == id }
    }

    override suspend fun getProductByIds(ids: List<Long>): List<Product> {
        return MockData.MockProducts.filter { ids.contains(it.id) }
    }

    override suspend fun addProduct(product: Product) {
        MockData.MockProducts.add(product)
    }
}

package com.devhjs.ttackjigeum_android.data.repository.mock

import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockProductRepositoryImpl : ProductRepository {
    override fun getProducts(): Flow<List<Product>> {
        return flowOf(MockData.MockProducts)
    }

    override fun getProductById(id: Long): Flow<Product?> {
        return flowOf(MockData.MockProducts.find { it.id == id })
    }
}

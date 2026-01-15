package com.devhjs.ttackjigeum_android.data.repository

import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository

class ProdProductRepositoryImpl : ProductRepository {
    override suspend fun getProducts(): List<Product> {
        // TODO: Implement Firestore fetching
        return emptyList()
    }

    override suspend fun getProductById(id: Long): Product? {
        // TODO: Implement Firestore fetching
        return null
    }

    override suspend fun getProductByIds(ids: List<Long>): List<Product> {
        // TODO: Implement Firestore fetching
        return emptyList()
    }

    override suspend fun addProduct(product: Product) {
        // TODO: Implement Firestore adding
    }
}

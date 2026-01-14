package com.devhjs.ttackjigeum_android.data.repository

import com.devhjs.ttackjigeum_android.data.local.dao.ProductDao
import com.devhjs.ttackjigeum_android.data.mapper.toDomain
import com.devhjs.ttackjigeum_android.data.mapper.toEntity
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val productDao: ProductDao
) : ProductRepository {

    override suspend fun getProducts(): List<Product> {
        return productDao.findAll()
            .map { entities -> entities.map { it.toDomain() } }
            .first()
    }

    override suspend fun getProductById(id: Long): Product? {
        return productDao.findById(id)
            .map { it?.toDomain() }
            .first()
    }

    override suspend fun addProduct(product: Product) {
        productDao.insert(product.toEntity())
    }

    override suspend fun deleteProduct(id: Long) {
        productDao.deleteByIds(listOf(id))
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.delete(product.toEntity())
    }
}

package com.devhjs.ttackjigeum_android.domain.repository

import com.devhjs.ttackjigeum_android.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    fun getProductById(id: Long): Flow<Product?>
}

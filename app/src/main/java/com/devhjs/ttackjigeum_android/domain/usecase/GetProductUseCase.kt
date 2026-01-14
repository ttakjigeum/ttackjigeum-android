package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository


class GetProductUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: Long): Result<Product?, DataError> {
        return try {
            val product = repository.getProductById(id)
            Result.Success(product)
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}

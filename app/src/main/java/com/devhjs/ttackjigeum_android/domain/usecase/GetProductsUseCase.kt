package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository

class GetProductsUseCase(
    private val productRepository: ProductRepository,
    private val userConfigRepository: UserConfigRepository
) {
    suspend operator fun invoke(): Result<List<Product>, DataError> {
        return try {
            val userConfigs = userConfigRepository.getAllUserConfigs()
            val productIds = userConfigs.map { it.productId }
            val products = productRepository.getProductByIds(productIds)

            val updatedProducts = products.map { product ->
                val config = userConfigs.find { it.productId == product.id }
                if (config != null) {
                    product.copy(targetPrice = config.targetPrice)
                } else {
                    product
                }
            }

            Result.Success(updatedProducts)
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}

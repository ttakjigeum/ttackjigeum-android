package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository

class GetProductsUseCase(
    private val productRepository: ProductRepository,
    private val userConfigRepository: UserConfigRepository,
    private val priceHistoryRepository: PriceHistoryRepository
) {
    suspend operator fun invoke(): Result<List<Product>, DataError> {
        return try {
            val userConfigs = userConfigRepository.getAllUserConfigs()
            val productIds = userConfigs.map { it.productId }
            val products = productRepository.getProductByIds(productIds)

            val productsWithPrice = products.map { product ->
                val priceHistories = priceHistoryRepository.getPriceHistories(product.id)
                val lowestPrice = if (priceHistories.isNotEmpty()) {
                    priceHistories.minOf { it.price }
                } else {
                    product.currentPrice
                }
                val averagePrice = if (priceHistories.isNotEmpty()) {
                    priceHistories.map { it.price }.average().toInt()
                } else {
                    product.currentPrice
                }
                product.copy(
                    lowestPrice = lowestPrice,
                    averagePrice = averagePrice
                )
            }

            Result.Success(productsWithPrice)
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}

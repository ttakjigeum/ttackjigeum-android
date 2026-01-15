package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository

class GetProductUseCase(
    private val repository: ProductRepository,
    private val priceHistoryRepository: PriceHistoryRepository
) {
    suspend operator fun invoke(productId: Long): Result<Product, DataError> {
        return try {
            val product = repository.getProductById(productId)
            if (product != null) {
                val priceHistories = priceHistoryRepository.getPriceHistories(productId)
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

                Result.Success(
                    product.copy(
                        lowestPrice = lowestPrice,
                        averagePrice = averagePrice
                    )
                )
            } else {
                Result.Error(DataError.Local.UNKNOWN)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}

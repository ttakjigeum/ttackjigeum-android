package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetProductsUseCase(
    private val productRepository: ProductRepository,
    private val userConfigRepository: UserConfigRepository,
    private val priceHistoryRepository: PriceHistoryRepository
) {
    operator fun invoke(): Flow<Result<List<Product>, DataError>> {
        return userConfigRepository.getAllUserConfigsFlow().map { userConfigs ->
            try {
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
        }.catch {
            emit(Result.Error(DataError.Network.UNKNOWN))
        }
    }
}

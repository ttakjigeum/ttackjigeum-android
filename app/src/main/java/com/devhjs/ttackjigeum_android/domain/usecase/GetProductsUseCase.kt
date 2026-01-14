package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository

import com.devhjs.ttackjigeum_android.domain.repository.RoomUserConfigRepository

class GetProductsUseCase(
    private val productRepository: ProductRepository,
    private val userConfigRepository: RoomUserConfigRepository
) {
    suspend operator fun invoke(): Result<List<Product>, DataError> {
        return try {
            val userConfigs = userConfigRepository.getAllUserConfigs()
            val productIds = userConfigs.map { it.productId }
            val products = productRepository.getProductByIds(productIds)
            Result.Success(products)
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}

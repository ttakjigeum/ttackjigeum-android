package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository

class DeleteProductUseCase(
    private val productRepository: ProductRepository,
    private val userConfigRepository: UserConfigRepository
) {
    suspend operator fun invoke(productId: Long): Result<Unit, Unit> {
        return try {
            productRepository.deleteProduct(productId)
            userConfigRepository.deleteUserConfig(productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(Unit)
        }
    }
}

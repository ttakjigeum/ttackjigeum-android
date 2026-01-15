package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.RoomUserConfigRepository

class AddProductUseCase(
    private val productRepository: ProductRepository,
    private val userConfigRepository: RoomUserConfigRepository
) {
    suspend operator fun invoke(url: String): Result<Unit, DataError> {
        return try {
            val newId = System.currentTimeMillis()
            
            // Randomly select one of the existing mock products to copy properties from
            val templateProduct = productRepository.getProducts().randomOrNull() ?: Product(
                id = newId, // Fallback if no products exist
                name = "기본 상품",
                originalPrice = 10000,
                currentPrice = 9000,
                targetPrice = 8000,
                lowestPrice = 8500,
                averagePrice = 9500,
                isFavorite = false,
                url = url,
                imageUrl = "https://via.placeholder.com/150"
            )

            val newProduct = templateProduct.copy(
                id = newId,
                url = url, // Use the provided URL
                isFavorite = false // Reset favorite status
            )
            
            productRepository.addProduct(newProduct)
            
            val newUserConfig = UserConfig(
                productId = newId,
                targetPrice = newProduct.targetPrice,
                notificationEnabled = true
            )
            userConfigRepository.saveUserConfig(newUserConfig)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}

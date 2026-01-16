package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.parser.ProductParser
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository

class AddProductUseCase(
    private val productRepository: ProductRepository,
    private val productParser: ProductParser,
    private val userConfigRepository: UserConfigRepository,
) {
    suspend operator fun invoke(url: String): Result<Unit, DataError> {
        return try {
            val userConfigs = userConfigRepository.getAllUserConfigs()
            val userConfigProductIds = userConfigs.map { it.productId }
            val existingProducts = productRepository.getProductByIds(userConfigProductIds)

            if (existingProducts.any { it.url == url }) {
                return Result.Error(DataError.Local.DUPLICATE)
            }

            val parseResult = productParser.parseProduct(url)

            when (parseResult) {
                is Result.Success -> {
                    val parsedData = parseResult.data
                    val newId = System.currentTimeMillis()

                    val newProduct = Product(
                        id = newId,
                        name = parsedData.name,
                        originalPrice = parsedData.originalPrice,
                        currentPrice = parsedData.currentPrice,
                        targetPrice = (parsedData.currentPrice * 0.9).toInt(), // Default target price 90%
                        lowestPrice = parsedData.currentPrice,
                        averagePrice = parsedData.currentPrice,
                        isFavorite = false,
                        url = parsedData.url,
                        imageUrl = parsedData.imageUrl
                    )

                    productRepository.addProduct(newProduct)

                    val newUserConfig = UserConfig(
                        productId = newProduct.id,
                        targetPrice = newProduct.targetPrice,
                        notificationEnabled = true,
                    )
                    userConfigRepository.saveUserConfig(newUserConfig)

                    Result.Success(Unit)
                }
                is Result.Error -> {
                    Result.Error(parseResult.error)
                }
            }
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}

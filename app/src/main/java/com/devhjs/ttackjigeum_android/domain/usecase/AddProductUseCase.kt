package com.devhjs.ttackjigeum_android.domain.usecase

import android.util.Log
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
    suspend operator fun invoke(originalUrl: String): Result<Unit, DataError> {
        return try {
            val parseResult = productParser.parseProduct(originalUrl)

            when (parseResult) {
                is Result.Success -> {
                    val parsedData = parseResult.data
                    
                    // URL 정규화 (쿼리 파라미터 제거)
                    val normalizedUrl = if (parsedData.url.contains("?")) {
                        parsedData.url.substringBefore("?")
                    } else {
                        parsedData.url
                    }

                    val userConfigs = userConfigRepository.getAllUserConfigs()
                    val userConfigProductIds = userConfigs.map { it.productId }
                    val existingProducts = productRepository.getProductByIds(userConfigProductIds)

                    if (existingProducts.any { 
                            val existingNormalized = if (it.url.contains("?")) it.url.substringBefore("?") else it.url
                            existingNormalized == normalizedUrl 
                        }) {
                        return Result.Error(DataError.Local.DUPLICATE)
                    }

                    val newId = System.currentTimeMillis()

                    val newProduct = Product(
                        id = newId,
                        name = parsedData.name,
                        originalPrice = parsedData.originalPrice,
                        currentPrice = parsedData.currentPrice,
                        targetPrice = (parsedData.currentPrice * 0.9).toInt(), // 기본 목표가 90% 설정
                        lowestPrice = parsedData.currentPrice,
                        averagePrice = parsedData.currentPrice,
                        isFavorite = false,
                        url = normalizedUrl,
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
            Log.e("AddProductUseCase", "상품 추가 중 오류 발생", e)
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}

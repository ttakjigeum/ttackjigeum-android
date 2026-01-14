package com.devhjs.ttackjigeum_android.data.mapper

import com.devhjs.ttackjigeum_android.data.local.entity.ProductEntity
import com.devhjs.ttackjigeum_android.data.remote.dto.ProductDto
import com.devhjs.ttackjigeum_android.domain.model.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        originalPrice = originalPrice,
        currentPrice = currentPrice,
        targetPrice = targetPrice,
        lowestPrice = lowestPrice,
        averagePrice = averagePrice,
        isFavorite = isFavorite,
        url = url,
        imageUrl = imageUrl
    )
}

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        originalPrice = originalPrice,
        currentPrice = currentPrice,
        targetPrice = targetPrice,
        lowestPrice = lowestPrice,
        averagePrice = averagePrice,
        isFavorite = isFavorite,
        url = url,
        imageUrl = imageUrl
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        originalPrice = originalPrice,
        currentPrice = currentPrice,
        targetPrice = targetPrice,
        lowestPrice = lowestPrice,
        averagePrice = averagePrice,
        isFavorite = isFavorite,
        url = url,
        imageUrl = imageUrl
    )
}

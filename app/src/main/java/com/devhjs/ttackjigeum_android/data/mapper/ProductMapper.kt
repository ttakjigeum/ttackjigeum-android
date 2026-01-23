package com.devhjs.ttackjigeum_android.data.mapper

import com.devhjs.ttackjigeum_android.data.dto.ProductDto
import com.devhjs.ttackjigeum_android.domain.model.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = id ?: 0L,
        name = name ?: "",
        originalPrice = originalPrice ?: 0,
        currentPrice = currentPrice ?: 0,
        targetPrice = 0,
        lowestPrice = 0,
        averagePrice = 0,
        isFavorite = favorite ?: false,
        url = url ?: "",
        imageUrl = imageUrl ?: ""
    )
}

fun Product.toDto(): ProductDto {
    return ProductDto(
        id = id,
        name = name,
        originalPrice = originalPrice,
        currentPrice = currentPrice,
        favorite = isFavorite,
        url = url,
        imageUrl = imageUrl
    )
}

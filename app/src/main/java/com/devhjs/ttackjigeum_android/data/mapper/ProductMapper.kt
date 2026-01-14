package com.devhjs.ttackjigeum_android.data.mapper

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
        imageUrl = imageUrl,
    )
}

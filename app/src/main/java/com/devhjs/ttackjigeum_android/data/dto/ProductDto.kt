package com.devhjs.ttackjigeum_android.data.remote.dto

import com.devhjs.ttackjigeum_android.domain.model.PriceHistory

data class ProductDto(
    val id: Long,
    val name: String,
    val originalPrice: Int,
    val currentPrice: Int,
    val targetPrice: Int,
    val lowestPrice: Int,
    val averagePrice: Int,
    val isFavorite: Boolean,
    val url: String,
    val imageUrl: String
)

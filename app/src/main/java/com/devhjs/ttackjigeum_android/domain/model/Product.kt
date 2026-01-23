package com.devhjs.ttackjigeum_android.domain.model

data class Product(
    val id: Long,
    val name: String,
    val originalPrice: Int,
    val currentPrice: Int,
    val targetPrice: Int,
    val lowestPrice: Int,
    val averagePrice: Int,
    val isFavorite: Boolean,
    val url: String,
    val imageUrl: String,
)

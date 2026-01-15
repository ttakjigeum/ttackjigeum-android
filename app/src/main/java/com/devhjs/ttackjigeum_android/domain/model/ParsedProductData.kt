package com.devhjs.ttackjigeum_android.domain.model

data class ParsedProductData(
    val name: String,
    val originalPrice: Int,
    val currentPrice: Int,
    val url: String,
    val imageUrl: String,
)

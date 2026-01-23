package com.devhjs.ttackjigeum_android.data.dto



data class ProductDto(
    val id: Long? = null,
    val name: String? = null,
    val originalPrice: Int? = null,
    val currentPrice: Int? = null,
    val favorite: Boolean? = null,
    val url: String? = null,
    val imageUrl: String? = null
)


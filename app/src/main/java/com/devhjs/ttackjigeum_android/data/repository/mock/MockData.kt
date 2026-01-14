package com.devhjs.ttackjigeum_android.data.repository.mock

import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.model.Product

object MockData {
    val MockProducts = listOf(
        Product(
            id = 1L,
            name = "신라면 (5봉)",
            originalPrice = 4500,
            currentPrice = 3900,
            targetPrice = 3500,
            lowestPrice = 3400,
            averagePrice = 4100,
            isFavorite = true,
            url = "https://example.com/product/1",
            imageUrl = "https://img.example.com/shin.jpg"
        ),
        Product(
            id = 2L,
            name = "서울우유 1L",
            originalPrice = 2980,
            currentPrice = 2850,
            targetPrice = 2500,
            lowestPrice = 2480,
            averagePrice = 2900,
            isFavorite = false,
            url = "https://example.com/product/2",
            imageUrl = "https://img.example.com/milk.jpg"
        ),
        Product(
            id = 3L,
            name = "코카콜라 500ml",
            originalPrice = 2100,
            currentPrice = 1900,
            targetPrice = 1500,
            lowestPrice = 1450,
            averagePrice = 2000,
            isFavorite = true,
            url = "https://example.com/product/3",
            imageUrl = "https://img.example.com/coke.jpg"
        )
    )

    val MockPriceHistories = mapOf(
        1L to listOf(
            PriceHistory("2024-01-10", 4500),
            PriceHistory("2024-01-11", 4300),
            PriceHistory("2024-01-12", 3900)
        ),
        2L to listOf(
            PriceHistory("2024-01-08", 2980),
            PriceHistory("2024-01-12", 2850)
        ),
        3L to listOf(
            PriceHistory("2024-01-05", 2100),
            PriceHistory("2024-01-07", 2000),
            PriceHistory("2024-01-12", 1900)
        )
    )
}

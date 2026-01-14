package com.devhjs.ttackjigeum_android.domain.repository

import com.devhjs.ttackjigeum_android.domain.model.PriceHistory


interface PriceHistoryRepository {
    suspend fun getPriceHistories(productId: Long): List<PriceHistory>
}

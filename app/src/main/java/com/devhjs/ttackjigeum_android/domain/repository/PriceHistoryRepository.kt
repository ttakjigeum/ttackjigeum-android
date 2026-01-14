package com.devhjs.ttackjigeum_android.domain.repository

import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import kotlinx.coroutines.flow.Flow

interface PriceHistoryRepository {
    fun getPriceHistories(productId: Long): Flow<List<PriceHistory>>
}

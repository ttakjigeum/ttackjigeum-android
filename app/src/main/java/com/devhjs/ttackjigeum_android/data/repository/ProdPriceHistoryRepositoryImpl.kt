package com.devhjs.ttackjigeum_android.data.repository

import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository

class ProdPriceHistoryRepositoryImpl : PriceHistoryRepository {
    override suspend fun getPriceHistories(productId: Long): List<PriceHistory> {
        // TODO: Implement Firestore fetching
        return emptyList()
    }
}

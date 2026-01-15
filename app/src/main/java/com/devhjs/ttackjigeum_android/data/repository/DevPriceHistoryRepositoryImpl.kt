package com.devhjs.ttackjigeum_android.data.repository

import com.devhjs.ttackjigeum_android.data.repository.mock.MockData
import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository

class DevPriceHistoryRepositoryImpl : PriceHistoryRepository {
    override suspend fun getPriceHistories(productId: Long): List<PriceHistory> {
        // Mock data logic from MockPriceHistoryRepositoryImpl
        return MockData.MockPriceHistories[productId] ?: emptyList()
    }
}

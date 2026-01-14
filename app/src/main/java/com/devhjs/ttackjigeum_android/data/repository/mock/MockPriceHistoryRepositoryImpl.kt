package com.devhjs.ttackjigeum_android.data.repository.mock

import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository


class MockPriceHistoryRepositoryImpl : PriceHistoryRepository {
    override suspend fun getPriceHistories(productId: Long): List<PriceHistory> {
        return MockData.MockPriceHistories[productId] ?: emptyList()
    }
}

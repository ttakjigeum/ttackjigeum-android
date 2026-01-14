package com.devhjs.ttackjigeum_android.data.repository.mock

import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockPriceHistoryRepositoryImpl : PriceHistoryRepository {
    override fun getPriceHistories(productId: Long): Flow<List<PriceHistory>> {
        return flowOf(MockData.MockPriceHistories[productId] ?: emptyList())
    }
}

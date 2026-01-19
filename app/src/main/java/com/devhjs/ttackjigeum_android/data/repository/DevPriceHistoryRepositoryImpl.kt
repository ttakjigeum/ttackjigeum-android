package com.devhjs.ttackjigeum_android.data.repository

import com.devhjs.ttackjigeum_android.data.repository.mock.MockData
import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository

class DevPriceHistoryRepositoryImpl : PriceHistoryRepository {
    override suspend fun getPriceHistories(productId: Long): List<PriceHistory> {
        // MockPriceHistoryRepositoryImpl에서 사용하는 테스트용 데이터 로직
        return MockData.MockPriceHistories[productId] ?: emptyList()
    }
}

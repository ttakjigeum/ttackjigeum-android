package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository


class GetPriceHistoryUseCase(
    private val repository: PriceHistoryRepository
) {
    suspend operator fun invoke(productId: Long): Result<List<PriceHistory>, DataError> {
        return try {
            val history = repository.getPriceHistories(productId)
            Result.Success(history)
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}

package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository

class UpdateTargetPriceUseCase(
    private val repository: UserConfigRepository
) {
    suspend operator fun invoke(productId: Long, targetPrice: Int): Result<Unit, DataError> {
        return try {
            repository.updateTargetPrice(productId, targetPrice)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}

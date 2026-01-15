package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository

class GetUserConfigUseCase(
    private val repository: UserConfigRepository
) {
    suspend operator fun invoke(productId: Long): UserConfig? {
        return repository.getUserConfig(productId)
    }
}

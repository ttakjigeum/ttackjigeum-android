package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository

class ToggleProductNotificationUseCase(
    private val repository: UserConfigRepository
) {
    suspend operator fun invoke(productId: Long, currentTargetPrice: Int) {
        val currentConfig = repository.getUserConfig(productId)
        if (currentConfig != null) {
            val newConfig = currentConfig.copy(notificationEnabled = !currentConfig.notificationEnabled)
            repository.updateUserConfig(newConfig)
        } else {
            val newConfig = UserConfig(
                productId = productId,
                targetPrice = currentTargetPrice,
                notificationEnabled = true
            )
            repository.saveUserConfig(newConfig)
        }
    }
}

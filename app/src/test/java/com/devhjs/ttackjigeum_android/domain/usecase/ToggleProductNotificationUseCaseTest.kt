package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ToggleProductNotificationUseCaseTest {

    private lateinit var userConfigRepository: UserConfigRepository
    private lateinit var toggleProductNotificationUseCase: ToggleProductNotificationUseCase

    @Before
    fun setUp() {
        userConfigRepository = mockk(relaxed = true)
        toggleProductNotificationUseCase = ToggleProductNotificationUseCase(userConfigRepository)
    }

    @Test
    fun `invoke updates config when it exists`() = runTest {
        // Given
        val productId = 1L
        val currentTargetPrice = 10000
        val existingConfig = UserConfig(
            productId = productId,
            targetPrice = currentTargetPrice,
            notificationEnabled = true
        )
        coEvery { userConfigRepository.getUserConfig(productId) } returns existingConfig

        // When
        toggleProductNotificationUseCase(productId, currentTargetPrice)

        // Then
        val slot = slot<UserConfig>()
        coVerify { userConfigRepository.updateUserConfig(capture(slot)) }
        assertEquals(false, slot.captured.notificationEnabled)
    }

    @Test
    fun `invoke creates config when it does not exist`() = runTest {
        // Given
        val productId = 1L
        val currentTargetPrice = 10000
        coEvery { userConfigRepository.getUserConfig(productId) } returns null

        // When
        toggleProductNotificationUseCase(productId, currentTargetPrice)

        // Then
        val slot = slot<UserConfig>()
        coVerify { userConfigRepository.saveUserConfig(capture(slot)) }
        assertEquals(true, slot.captured.notificationEnabled)
        assertEquals(currentTargetPrice, slot.captured.targetPrice)
        assertEquals(productId, slot.captured.productId)
    }
}

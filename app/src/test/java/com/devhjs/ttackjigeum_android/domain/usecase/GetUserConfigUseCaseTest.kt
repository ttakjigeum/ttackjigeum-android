package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetUserConfigUseCaseTest {

    private lateinit var userConfigRepository: UserConfigRepository
    private lateinit var getUserConfigUseCase: GetUserConfigUseCase

    @Before
    fun setUp() {
        userConfigRepository = mockk()
        getUserConfigUseCase = GetUserConfigUseCase(userConfigRepository)
    }

    @Test
    fun `invoke returns user config when exists`() = runTest {
        // Given
        val productId = 1L
        val config = UserConfig(
            productId = productId,
            targetPrice = 10000,
            notificationEnabled = true
        )
        coEvery { userConfigRepository.getUserConfig(productId) } returns config

        // When
        val result = getUserConfigUseCase(productId)

        // Then
        assertEquals(config, result)
    }

    @Test
    fun `invoke returns null when user config does not exist`() = runTest {
        // Given
        val productId = 1L
        coEvery { userConfigRepository.getUserConfig(productId) } returns null

        // When
        val result = getUserConfigUseCase(productId)

        // Then
        assertNull(result)
    }
}

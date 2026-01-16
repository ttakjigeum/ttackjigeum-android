package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateTargetPriceUseCaseTest {

    private lateinit var userConfigRepository: UserConfigRepository
    private lateinit var updateTargetPriceUseCase: UpdateTargetPriceUseCase

    @Before
    fun setUp() {
        userConfigRepository = mockk(relaxed = true)
        updateTargetPriceUseCase = UpdateTargetPriceUseCase(userConfigRepository)
    }

    @Test
    fun `invoke updates target price successfully`() = runTest {
        // Given
        val productId = 1L
        val targetPrice = 9000
        coEvery { userConfigRepository.updateTargetPrice(productId, targetPrice) } returns Unit

        // When
        val result = updateTargetPriceUseCase(productId, targetPrice)

        // Then
        assertTrue(result is Result.Success)
        coVerify { userConfigRepository.updateTargetPrice(productId, targetPrice) }
    }

    @Test
    fun `invoke returns error when exception occurs`() = runTest {
        // Given
        val productId = 1L
        val targetPrice = 9000
        coEvery { userConfigRepository.updateTargetPrice(productId, targetPrice) } throws Exception("DB Error")

        // When
        val result = updateTargetPriceUseCase(productId, targetPrice)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(DataError.Local.UNKNOWN, (result as Result.Error).error)
    }
}

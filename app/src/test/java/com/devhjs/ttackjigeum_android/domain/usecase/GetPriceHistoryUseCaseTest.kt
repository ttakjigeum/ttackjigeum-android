package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPriceHistoryUseCaseTest {

    private lateinit var priceHistoryRepository: PriceHistoryRepository
    private lateinit var getPriceHistoryUseCase: GetPriceHistoryUseCase

    @Before
    fun setUp() {
        priceHistoryRepository = mockk()
        getPriceHistoryUseCase = GetPriceHistoryUseCase(priceHistoryRepository)
    }

    @Test
    fun `invoke returns success with history list`() = runTest {
        // Given
        val productId = 1L
        val histories = listOf(
            PriceHistory(price = 10000, datetime = "2023-01-01"),
            PriceHistory(price = 12000, datetime = "2023-01-02")
        )
        coEvery { priceHistoryRepository.getPriceHistories(productId) } returns histories

        // When
        val result = getPriceHistoryUseCase(productId)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(histories, (result as Result.Success).data)
    }

    @Test
    fun `invoke returns error when exception occurs`() = runTest {
        // Given
        val productId = 1L
        coEvery { priceHistoryRepository.getPriceHistories(productId) } throws Exception("Network error")

        // When
        val result = getPriceHistoryUseCase(productId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(DataError.Network.UNKNOWN, (result as Result.Error).error)
    }
}

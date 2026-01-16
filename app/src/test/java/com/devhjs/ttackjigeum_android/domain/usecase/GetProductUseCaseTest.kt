package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetProductUseCaseTest {

    private lateinit var productRepository: ProductRepository
    private lateinit var priceHistoryRepository: PriceHistoryRepository
    private lateinit var getProductUseCase: GetProductUseCase

    @Before
    fun setUp() {
        productRepository = mockk()
        priceHistoryRepository = mockk()
        getProductUseCase = GetProductUseCase(productRepository, priceHistoryRepository)
    }

    @Test
    fun `invoke returns success with lowest and average price when history exists`() = runTest {
        // Given
        val productId = 1L
        val currentPrice = 10000
        val historyPrice1 = 8000
        val historyPrice2 = 12000
        val expectedLowestPrice = 8000
        val expectedAveragePrice = 10000

        val product = Product(
            id = productId,
            name = "Test Product",
            currentPrice = currentPrice,
            originalPrice = currentPrice,
            targetPrice = currentPrice,
            lowestPrice = currentPrice, // These will be overwritten by copy if needed, or tested against
            averagePrice = currentPrice,
            isFavorite = false,
            imageUrl = "test.jpg",
            url = "http://test.com"
        )
        val histories = listOf(
            PriceHistory(price = historyPrice1, datetime = "2023-01-01"),
            PriceHistory(price = historyPrice2, datetime = "2023-01-02")
        )

        coEvery { productRepository.getProductById(productId) } returns product
        coEvery { priceHistoryRepository.getPriceHistories(productId) } returns histories

        // When
        val result = getProductUseCase(productId)

        // Then
        assertTrue(result is Result.Success)
        val successData = (result as Result.Success).data
        assertEquals(expectedLowestPrice, successData.lowestPrice)
        assertEquals(expectedAveragePrice, successData.averagePrice)
    }

    @Test
    fun `invoke returns success with current price when no history exists`() = runTest {
        // Given
        val productId = 1L
        val currentPrice = 10000
        val product = Product(
            id = productId,
            name = "Test Product",
            currentPrice = currentPrice,
            originalPrice = currentPrice,
            targetPrice = currentPrice,
            lowestPrice = currentPrice,
            averagePrice = currentPrice,
            isFavorite = false,
            imageUrl = "test.jpg",
            url = "http://test.com"
        )

        coEvery { productRepository.getProductById(productId) } returns product
        coEvery { priceHistoryRepository.getPriceHistories(productId) } returns emptyList()

        // When
        val result = getProductUseCase(productId)

        // Then
        assertTrue(result is Result.Success)
        val successData = (result as Result.Success).data
        assertEquals(currentPrice, successData.lowestPrice)
        assertEquals(currentPrice, successData.averagePrice)
    }

    @Test
    fun `invoke returns error when product not found`() = runTest {
        // Given
        val productId = 1L
        coEvery { productRepository.getProductById(productId) } returns null

        // When
        val result = getProductUseCase(productId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(DataError.Local.UNKNOWN, (result as Result.Error).error)
    }

    @Test
    fun `invoke returns error when exception occurs`() = runTest {
        // Given
        val productId = 1L
        coEvery { productRepository.getProductById(productId) } throws Exception("Network error")

        // When
        val result = getProductUseCase(productId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(DataError.Network.UNKNOWN, (result as Result.Error).error)
    }
}

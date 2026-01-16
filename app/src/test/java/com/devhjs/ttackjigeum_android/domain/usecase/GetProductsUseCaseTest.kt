package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetProductsUseCaseTest {

    private lateinit var productRepository: ProductRepository
    private lateinit var userConfigRepository: UserConfigRepository
    private lateinit var priceHistoryRepository: PriceHistoryRepository
    private lateinit var getProductsUseCase: GetProductsUseCase

    @Before
    fun setUp() {
        productRepository = mockk(relaxed = true)
        userConfigRepository = mockk(relaxed = true)
        priceHistoryRepository = mockk(relaxed = true)
        getProductsUseCase = GetProductsUseCase(
            productRepository = productRepository,
            userConfigRepository = userConfigRepository,
            priceHistoryRepository = priceHistoryRepository
        )
    }

    @Test
    fun `invoke success returns matched products together with target price from config`() =
        runTest {
            val productId = 1L
            val configTargetPrice = 5000

            val userConfig = UserConfig(
                productId = productId,
                targetPrice = configTargetPrice,
                notificationEnabled = true
            )

            val product = Product(
                id = productId,
                name = "Test Product",
                originalPrice = 10000,
                currentPrice = 6000,
                targetPrice = 0, // Initial target price differs from config
                lowestPrice = 6000,
                averagePrice = 6000,
                isFavorite = false,
                url = "http://test.com",
                imageUrl = "http://test.com/img.jpg"
            )

            // Given
            every { userConfigRepository.getAllUserConfigsFlow() } returns flowOf(listOf(userConfig))
            coEvery { productRepository.getProductByIds(listOf(productId)) } returns listOf(product)

            // When
            val result = getProductsUseCase().first()

            // Then
            assertTrue(result is Result.Success)
            val products = (result as Result.Success).data
            assertEquals(1, products.size)
            assertEquals(
                configTargetPrice,
                products[0].targetPrice
            ) // Verify targetPrice is updated
        }

    @Test
    fun `invoke returns error when exception occurs`() = runTest {
        // Given
        every { userConfigRepository.getAllUserConfigsFlow() } returns flow {
            throw RuntimeException(
                "DB Error"
            )
        }

        // When
        val result = getProductsUseCase().first()

        // Then
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertEquals(DataError.Network.UNKNOWN, error)
    }
}
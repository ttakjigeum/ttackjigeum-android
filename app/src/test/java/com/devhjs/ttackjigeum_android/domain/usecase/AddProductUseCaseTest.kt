package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.ParsedProductData
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.parser.ProductParser
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddProductUseCaseTest {

    private lateinit var productRepository: ProductRepository
    private lateinit var productParser: ProductParser
    private lateinit var userConfigRepository: UserConfigRepository
    private lateinit var addProductUseCase: AddProductUseCase

    @Before
    fun setUp() {
        productRepository = mockk(relaxed = true)
        productParser = mockk()
        userConfigRepository = mockk(relaxed = true)
        addProductUseCase = AddProductUseCase(
            productRepository = productRepository,
            productParser = productParser,
            userConfigRepository = userConfigRepository
        )
    }

    @Test
    fun `invoke success parses product and saves to repository`() = runTest {
        val url = "http://test.com/product"
        val parsedData = ParsedProductData(
            name = "Test Product",
            originalPrice = 10000,
            currentPrice = 9000,
            url = url,
            imageUrl = "http://test.com/image.jpg"
        )

        // Given
        coEvery { userConfigRepository.getAllUserConfigs() } returns emptyList()
        coEvery { productRepository.getProductByIds(any()) } returns emptyList()
        coEvery { productParser.parseProduct(url) } returns Result.Success(parsedData)

        // When
        val result = addProductUseCase(url)

        // Then
        assertTrue(result is Result.Success)
        coVerify { productParser.parseProduct(url) }
        coVerify { productRepository.addProduct(any()) }
        coVerify { userConfigRepository.saveUserConfig(any()) }
    }

    @Test
    fun `invoke returns duplicate error if url already exists`() = runTest {
        val url = "http://test.com/product"
        val existingProduct = Product(
            id = 1,
            name = "Existing Product",
            originalPrice = 10000,
            currentPrice = 9000,
            targetPrice = 8000,
            lowestPrice = 9000,
            averagePrice = 9000,
            isFavorite = false,
            url = url,
            imageUrl = ""
        )
        val userConfig = UserConfig(productId = 1, targetPrice = 8000, notificationEnabled = true)

        // Given
        coEvery { userConfigRepository.getAllUserConfigs() } returns listOf(userConfig)
        coEvery { productRepository.getProductByIds(listOf(1)) } returns listOf(existingProduct)

        // When
        val result = addProductUseCase(url)

        // Then
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertEquals(DataError.Local.DUPLICATE, error)

        coVerify(exactly = 0) { productParser.parseProduct(any()) }
    }

    @Test
    fun `invoke returns error if parser fails`() = runTest {
        val url = "http://test.com/invalid"

        // Given
        coEvery { userConfigRepository.getAllUserConfigs() } returns emptyList()
        coEvery { productRepository.getProductByIds(any()) } returns emptyList()
        coEvery { productParser.parseProduct(url) } returns Result.Error(DataError.Network.UNKNOWN)

        // When
        val result = addProductUseCase(url)

        // Then
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertEquals(DataError.Network.UNKNOWN, error)

        coVerify(exactly = 0) { productRepository.addProduct(any()) }
    }

    @Test
    fun `invoke returns error if exception occurs`() = runTest {
        val url = "http://test.com/product"

        // Given
        coEvery { userConfigRepository.getAllUserConfigs() } throws RuntimeException("Error")

        // When
        val result = addProductUseCase(url)

        // Then
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertEquals(DataError.Network.UNKNOWN, error)
    }
}
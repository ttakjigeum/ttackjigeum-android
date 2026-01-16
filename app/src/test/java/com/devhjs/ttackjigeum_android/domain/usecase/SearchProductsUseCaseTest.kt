package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchProductsUseCaseTest {

    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var searchProductsUseCase: SearchProductsUseCase

    private val sampleProducts = listOf(
        Product(1, "Apple iPhone", 1000, 900, 800, 900, 900, false, "url1", "img1"),
        Product(2, "Samsung Galaxy", 1000, 900, 800, 900, 900, false, "url2", "img2"),
        Product(3, "Apple iPad", 1000, 900, 800, 900, 900, false, "url3", "img3")
    )

    @Before
    fun setUp() {
        getProductsUseCase = mockk()
        searchProductsUseCase = SearchProductsUseCase(getProductsUseCase)
    }

    @Test
    fun `invoke with empty query returns all products`() = runTest {
        // Given
        every { getProductsUseCase() } returns flowOf(Result.Success(sampleProducts))

        // When
        val result = searchProductsUseCase("").first()

        // Then
        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(3, data.size)
    }

    @Test
    fun `invoke with query returns filtered products`() = runTest {
        // Given
        every { getProductsUseCase() } returns flowOf(Result.Success(sampleProducts))

        // When
        val result = searchProductsUseCase("Apple").first()

        // Then
        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(2, data.size) // iPhone and iPad
        assertTrue(data.all { it.name.contains("Apple") })
    }

    @Test
    fun `invoke with query is case insensitive`() = runTest {
        // Given
        every { getProductsUseCase() } returns flowOf(Result.Success(sampleProducts))

        // When
        val result = searchProductsUseCase("iphone").first()

        // Then
        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(1, data.size)
        assertEquals("Apple iPhone", data[0].name)
    }

    @Test
    fun `invoke propagates error from getProductsUseCase`() = runTest {
        // Given
        every { getProductsUseCase() } returns flowOf(Result.Error(DataError.Network.UNKNOWN))

        // When
        val result = searchProductsUseCase("query").first()

        // Then
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertEquals(DataError.Network.UNKNOWN, error)
    }
}
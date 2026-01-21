package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchProductsUseCaseTest {

    private lateinit var searchProductsUseCase: SearchProductsUseCase

    private val sampleProducts = listOf(
        Product(1, "Apple iPhone", 1000, 900, 800, 900, 900, false, "url1", "img1"),
        Product(2, "Samsung Galaxy", 1000, 900, 800, 900, 900, false, "url2", "img2"),
        Product(3, "Apple iPad", 1000, 900, 800, 900, 900, false, "url3", "img3")
    )

    @Before
    fun setUp() {
        searchProductsUseCase = SearchProductsUseCase()
    }

    @Test
    fun `invoke with empty query returns all products`() {
        // When
        val result = searchProductsUseCase(sampleProducts, "")

        // Then
        assertEquals(3, result.size)
        assertEquals(sampleProducts, result)
    }

    @Test
    fun `invoke with query returns filtered products`() {
        // When
        val result = searchProductsUseCase(sampleProducts, "Apple")

        // Then
        assertEquals(2, result.size) // iPhone and iPad
        assertTrue(result.all { it.name.contains("Apple") })
    }

    @Test
    fun `invoke with query is case insensitive`() {
        // When
        val result = searchProductsUseCase(sampleProducts, "iphone")

        // Then
        assertEquals(1, result.size)
        assertEquals("Apple iPhone", result[0].name)
    }
}

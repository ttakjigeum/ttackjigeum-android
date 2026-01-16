package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteProductUseCaseTest {

    private lateinit var productRepository: ProductRepository
    private lateinit var userConfigRepository: UserConfigRepository
    private lateinit var deleteProductUseCase: DeleteProductUseCase

    @Before
    fun setUp() {
        productRepository = mockk(relaxed = true)
        userConfigRepository = mockk(relaxed = true)
        deleteProductUseCase = DeleteProductUseCase(
            productRepository = productRepository,
            userConfigRepository = userConfigRepository
        )
    }

    @Test
    fun `invoke success deletes product and config`() = runTest {
        val productId = 123L

        // Given
        coEvery { productRepository.deleteProduct(productId) } returns Unit
        coEvery { userConfigRepository.deleteUserConfig(productId) } returns Unit

        // When
        val result = deleteProductUseCase(productId)

        // Then
        assertTrue(result is Result.Success)
        coVerify { productRepository.deleteProduct(productId) }
        coVerify { userConfigRepository.deleteUserConfig(productId) }
    }

    @Test
    fun `invoke returns error when exception occurs`() = runTest {
        val productId = 123L

        // Given
        coEvery { productRepository.deleteProduct(productId) } throws RuntimeException("DB Error")

        // When
        val result = deleteProductUseCase(productId)

        // Then
        assertTrue(result is Result.Error)
    }
}
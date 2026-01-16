package com.devhjs.ttackjigeum_android.presentation.detail

import app.cash.turbine.test
import com.devhjs.ttackjigeum_android.MainDispatcherRule
import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.model.UserConfig
import com.devhjs.ttackjigeum_android.domain.usecase.GetPriceHistoryUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.GetProductUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.GetUserConfigUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.ToggleProductNotificationUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.UpdateTargetPriceUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getProductUseCase: GetProductUseCase
    private lateinit var getPriceHistoryUseCase: GetPriceHistoryUseCase
    private lateinit var getUserConfigUseCase: GetUserConfigUseCase
    private lateinit var toggleProductNotificationUseCase: ToggleProductNotificationUseCase
    private lateinit var updateTargetPriceUseCase: UpdateTargetPriceUseCase
    private lateinit var viewModel: DetailViewModel

    private val productId = 1L
    private val mockProduct = Product(
        id = productId,
        name = "Test Product",
        originalPrice = 10000,
        currentPrice = 8000,
        targetPrice = 7000,
        lowestPrice = 8000,
        averagePrice = 9000,
        isFavorite = false,
        url = "http://test.com",
        imageUrl = "http://image.com"
    )
    private val mockHistories = listOf(
        PriceHistory(price = 8000, datetime = "2023-01-01"),
        PriceHistory(price = 9000, datetime = "2023-01-02")
    )
    private val mockUserConfig = UserConfig(
        productId = productId,
        targetPrice = 7500,
        notificationEnabled = true
    )

    @Before
    fun setUp() {
        getProductUseCase = mockk()
        getPriceHistoryUseCase = mockk()
        getUserConfigUseCase = mockk()
        toggleProductNotificationUseCase = mockk()
        updateTargetPriceUseCase = mockk()
    }

    private fun initViewModel() {
        viewModel = DetailViewModel(
            productId,
            getProductUseCase,
            getPriceHistoryUseCase,
            getUserConfigUseCase,
            toggleProductNotificationUseCase,
            updateTargetPriceUseCase
        )
    }

    @Test
    fun `Initialization loads data successfully and updates state`() = runTest {
        // Given
        coEvery { getProductUseCase(productId) } returns Result.Success(mockProduct)
        coEvery { getPriceHistoryUseCase(productId) } returns Result.Success(mockHistories)
        coEvery { getUserConfigUseCase(productId) } returns mockUserConfig

        // When
        initViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(mockHistories, state.priceHistories)
        
        // Check if user config target price is applied
        assertEquals(mockUserConfig.targetPrice, state.product?.targetPrice)
        assertEquals(mockUserConfig.notificationEnabled, state.isNotificationActive)
    }

    @Test
    fun `Initialization handles product load failure`() = runTest {
        // Given
        coEvery { getProductUseCase(productId) } returns Result.Error(DataError.Network.UNKNOWN)
        coEvery { getPriceHistoryUseCase(productId) } returns Result.Success(mockHistories)
        coEvery { getUserConfigUseCase(productId) } returns mockUserConfig

        // When
        initViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(null, state.product)
    }

    @Test
    fun `OnBackClick emits NavigateBack event`() = runTest {
        // Given
        coEvery { getProductUseCase(productId) } returns Result.Success(mockProduct)
        coEvery { getPriceHistoryUseCase(productId) } returns Result.Success(mockHistories)
        coEvery { getUserConfigUseCase(productId) } returns mockUserConfig
        initViewModel()

        viewModel.event.test {
            // When
            viewModel.onAction(DetailAction.OnBackClick)
            
            // Then
            assertEquals(DetailEvent.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `OnPurchaseClick emits OpenUrl event`() = runTest {
        // Given
        coEvery { getProductUseCase(productId) } returns Result.Success(mockProduct)
        coEvery { getPriceHistoryUseCase(productId) } returns Result.Success(mockHistories)
        coEvery { getUserConfigUseCase(productId) } returns mockUserConfig
        initViewModel()
        advanceUntilIdle()

        viewModel.event.test {
            // When
            viewModel.onAction(DetailAction.OnPurchaseClick)
            
            // Then
            val event = awaitItem()
            assertTrue(event is DetailEvent.OpenUrl)
            assertEquals(mockProduct.url, (event as DetailEvent.OpenUrl).url)
        }
    }

    @Test
    fun `OnNotificationToggle calls usecase and updates state`() = runTest {
        // Given
        // Initial config: enabled = true
        coEvery { getProductUseCase(productId) } returns Result.Success(mockProduct)
        coEvery { getPriceHistoryUseCase(productId) } returns Result.Success(mockHistories)
        coEvery { getUserConfigUseCase(productId) } returns mockUserConfig
        
        // After toggle calls usecase
        coEvery { toggleProductNotificationUseCase(productId, any()) } returns Unit
        
        // After toggle finishes, viewModel re-fetches config
        val toggledConfig = mockUserConfig.copy(notificationEnabled = false)
        coEvery { getUserConfigUseCase(productId) } returnsMany listOf(mockUserConfig, toggledConfig)

        initViewModel()
        advanceUntilIdle()

        // When
        viewModel.onAction(DetailAction.OnNotificationToggle)
        advanceUntilIdle()

        // Then
        coVerify { toggleProductNotificationUseCase(productId, mockUserConfig.targetPrice) }
        assertFalse(viewModel.state.value.isNotificationActive)
    }

    @Test
    fun `OnTargetPriceChange calls usecase and optimism updates state`() = runTest {
        // Given
        coEvery { getProductUseCase(productId) } returns Result.Success(mockProduct)
        coEvery { getPriceHistoryUseCase(productId) } returns Result.Success(mockHistories)
        coEvery { getUserConfigUseCase(productId) } returns mockUserConfig
        coEvery { updateTargetPriceUseCase(productId, any()) } returns Result.Success(Unit)
        
        initViewModel()
        advanceUntilIdle()

        val newTargetPrice = 5000

        // When
        viewModel.onAction(DetailAction.OnTargetPriceChange(newTargetPrice))
        advanceUntilIdle()

        // Then
        coVerify { updateTargetPriceUseCase(productId, newTargetPrice) }
        assertEquals(newTargetPrice, viewModel.state.value.product?.targetPrice)
    }
}

package com.devhjs.ttackjigeum_android.presentation.list

import app.cash.turbine.test
import com.devhjs.ttackjigeum_android.MainDispatcherRule
import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.usecase.GetProductsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var viewModel: ListViewModel

    @Before
    fun setUp() {
        getProductsUseCase = mockk()
    }

    private fun initViewModel() {
        viewModel = ListViewModel(getProductsUseCase)
    }

    @Test
    fun `Initial state verification on initialization`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Success(emptyList())
        initViewModel()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.products.isEmpty())
    }

    @Test
    fun `Loading state transition during product fetch`() = runTest {
        // Suspend the mock to verify loading state
        coEvery { getProductsUseCase() } coAnswers {
            delay(100)
            Result.Success(emptyList())
        }
        initViewModel()

        viewModel.state.test {
            // Initial state (might be loading=true already because init block runs fast)
            val initialState = awaitItem()
            
            // If the first emitted state is already loading=true, that's fine.
            // If it was false initially, we expect a true.
            if (!initialState.isLoading) {
                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)
            } else {
                assertTrue(initialState.isLoading)
            }
            
            // Eventually it should become false
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
        }
    }

    @Test
    fun `Successful product data retrieval update`() = runTest {
        val mockProducts = listOf(
            Product(
                id = 1,
                name = "Test Product",
                originalPrice = 1000,
                currentPrice = 800,
                targetPrice = 900,
                lowestPrice = 700,
                averagePrice = 900,
                isFavorite = false,
                url = "http://test.com",
                imageUrl = "http://image.com"
            )
        )
        coEvery { getProductsUseCase() } returns Result.Success(mockProducts)
        
        initViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(mockProducts, state.products)
    }

    @Test
    fun `Empty list handling on success`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Success(emptyList())
        
        initViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.products.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `Error handling during product fetch`() = runTest {
        coEvery { getProductsUseCase() } coAnswers {
            delay(1)
            Result.Error(DataError.Network.UNKNOWN)
        }
        
        initViewModel()
        
        viewModel.event.test {
            val event = awaitItem()
            assertTrue(event is ListEvent.ShowToast)
            assertEquals("상품 정보를 불러오는데 실패했습니다.", (event as ListEvent.ShowToast).message)
        }
        
        val state = viewModel.state.value
        assertFalse(state.isLoading)
    }

    @Test
    fun `OnProductClick navigation event emission`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Success(emptyList())
        initViewModel()
        
        val product = Product(
            id = 123,
            name = "Test",
            originalPrice = 0,
            currentPrice = 0,
            targetPrice = 0,
            lowestPrice = 0,
            averagePrice = 0,
            isFavorite = false,
            url = "",
            imageUrl = ""
        )

        viewModel.event.test {
            viewModel.onAction(ListAction.OnProductClick(product))
            val event = awaitItem()
            assertTrue(event is ListEvent.NavigateToDetail)
            assertEquals(123L, (event as ListEvent.NavigateToDetail).productId)
        }
    }

    @Test
    fun `OnAddLinkConfirm data refresh trigger`() = runTest {
        // First call
        coEvery { getProductsUseCase() } returns Result.Success(emptyList())
        initViewModel()
        advanceUntilIdle()

        // Prepare for second call (refresh)
        val newProducts = listOf(
            Product(id = 2, name = "New", originalPrice = 0, currentPrice = 0, targetPrice = 0, lowestPrice = 0, averagePrice = 0, isFavorite = false, url = "", imageUrl = "")
        )
        coEvery { getProductsUseCase() } returns Result.Success(newProducts)

        viewModel.onAction(ListAction.OnAddLinkConfirm("http://newlink.com"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(newProducts, state.products)
        
        // Verify it was called twice (once init, once refresh)
        coVerify(exactly = 2) { getProductsUseCase() }
    }

    @Test
    fun `Concurrent execution of loadProducts`() = runTest {
        // Simulate a slow network call
        coEvery { getProductsUseCase() } coAnswers {
            delay(50)
            Result.Success(emptyList())
        }
        initViewModel()
        
        // Trigger multiple refreshes rapidly
        repeat(5) {
            viewModel.onAction(ListAction.OnAddLinkConfirm("link"))
        }
        
        advanceUntilIdle()
        
        // Verify it was called 1 (init) + 5 (actions) times
        // In a real scenario, you might want to debounce, but the current impl doesn't debounce.
        coVerify(exactly = 6) { getProductsUseCase() }
    }

    @Test
    fun `SharedFlow event buffering and collection`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Success(emptyList())
        initViewModel()

        viewModel.event.test {
            viewModel.onAction(ListAction.OnProductClick(Product(1, "", 0, 0, 0, 0, 0, false, "", "")))
            
            val event = awaitItem()
            assertTrue(event is ListEvent.NavigateToDetail)
        }
    }

    @Test
    fun `Multiple error messages emission`() = runTest {
        coEvery { getProductsUseCase() } coAnswers {
            delay(1)
            Result.Error(DataError.Network.UNKNOWN)
        }
        initViewModel()
        
        // Initial load fails -> 1 event
        // Trigger refresh -> fails -> 2nd event
        
        viewModel.event.test {
            // First error from init
            assertEquals("상품 정보를 불러오는데 실패했습니다.", (awaitItem() as ListEvent.ShowToast).message)
            
            // Trigger refresh
            viewModel.onAction(ListAction.OnAddLinkConfirm("link"))
            
            // Second error from refresh
            assertEquals("상품 정보를 불러오는데 실패했습니다.", (awaitItem() as ListEvent.ShowToast).message)
        }
    }

    @Test
    fun `State immutability and copy logic`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Success(emptyList())
        initViewModel()
        
        val originalState = viewModel.state.value
        val modifiedState = originalState.copy(isLoading = true)
        
        assertFalse(originalState.isLoading)
        assertTrue(modifiedState.isLoading)
        // Ensure original state is not mutated
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `OnNotificationClick placeholder behavior`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Success(emptyList())
        initViewModel()
        
        val initialState = viewModel.state.value
        
        viewModel.onAction(ListAction.OnNotificationClick)
        
        // Should be no state change
        assertEquals(initialState, viewModel.state.value)
    }

}
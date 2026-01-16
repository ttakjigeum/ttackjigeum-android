package com.devhjs.ttackjigeum_android.presentation.list

import app.cash.turbine.test
import com.devhjs.ttackjigeum_android.MainDispatcherRule
import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.core.util.ShareIntentHandler
import com.devhjs.ttackjigeum_android.core.manager.ClipboardStateManager
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.usecase.AddProductUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.DeleteProductUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.SearchProductsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private lateinit var searchProductsUseCase: SearchProductsUseCase
    private lateinit var addProductUseCase: AddProductUseCase
    private lateinit var deleteProductUseCase: DeleteProductUseCase
    private lateinit var clipboardStateManager: ClipboardStateManager
    private lateinit var viewModel: ListViewModel

    @Before
    fun setUp() {
        searchProductsUseCase = mockk(relaxed = true)
        addProductUseCase = mockk(relaxed = true)
        deleteProductUseCase = mockk(relaxed = true)
        clipboardStateManager = mockk(relaxed = true)

        // Mock ShareIntentHandler to prevent state leakage between tests
        mockkObject(ShareIntentHandler)
        val testSharedUrl = MutableStateFlow<String?>(null)
        every { ShareIntentHandler.sharedUrl } returns testSharedUrl
        every { ShareIntentHandler.emitUrl(any()) } answers { testSharedUrl.value = firstArg() }
        every { ShareIntentHandler.consumeUrl() } answers { testSharedUrl.value = null }
    }

    @After
    fun tearDown() {
        unmockkObject(ShareIntentHandler)
    }

    private fun initViewModel() {
        viewModel = ListViewModel(
            searchProductsUseCase = searchProductsUseCase,
            addProductUseCase = addProductUseCase,
            deleteProductUseCase = deleteProductUseCase,
            clipboardStateManager = clipboardStateManager
        )
    }

    @Test
    // 초기화 시 초기 상태 검증
    fun `Initial state verification on initialization`() = runTest {
        coEvery { searchProductsUseCase(any()) } returns Result.Success(emptyList())
        initViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.products.isEmpty())
        assertTrue(state.searchQuery.isEmpty())
    }

    @Test
    // 검색어 업데이트 시 상품 검색 트리거
    fun `Search query update triggers product search`() = runTest {
        coEvery { searchProductsUseCase(any()) } returns Result.Success(emptyList())
        initViewModel()
        advanceUntilIdle()

        val query = "test query"
        viewModel.onAction(ListAction.OnSearchQueryChange(query))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(query, state.searchQuery)
        coVerify { searchProductsUseCase(query) }
    }

    @Test
    // 링크 추가 확인 시 상품 추가 유스케이스 트리거
    fun `OnAddLinkConfirm triggers add product use case`() = runTest {
        coEvery { searchProductsUseCase(any()) } returns Result.Success(emptyList())
        coEvery { addProductUseCase(any()) } returns Result.Success(Unit)

        initViewModel()
        advanceUntilIdle()

        val link = "http://test.com"
        viewModel.onAction(ListAction.OnAddLinkConfirm(link))
        advanceUntilIdle()

        coVerify { addProductUseCase(link) }
        io.mockk.verify { clipboardStateManager.markUrlProcessed(link) }
        coVerify(atLeast = 2) { searchProductsUseCase(any()) } // Initial + After Add
    }

    @Test
    // 스와이프 삭제 시 삭제할 상품 설정
    fun `OnSwipeDelete sets product to delete`() = runTest {
        coEvery { searchProductsUseCase(any()) } returns Result.Success(emptyList())
        initViewModel()
        advanceUntilIdle()

        val product = Product(
            id = 1, name = "Test", originalPrice = 0, currentPrice = 0,
            targetPrice = 0, lowestPrice = 0, averagePrice = 0,
            isFavorite = false, url = "", imageUrl = ""
        )

        viewModel.onAction(ListAction.OnSwipeDelete(product))

        assertEquals(product, viewModel.state.value.productToDelete)
    }

    @Test
    // 삭제 확인 시 상품 삭제 유스케이스 트리거
    fun `OnDeleteConfirm triggers delete product use case`() = runTest {
        coEvery { searchProductsUseCase(any()) } returns Result.Success(emptyList())
        coEvery { deleteProductUseCase(any()) } returns Result.Success(Unit)

        initViewModel()
        advanceUntilIdle()

        val product = Product(
            id = 1, name = "Test", originalPrice = 0, currentPrice = 0,
            targetPrice = 0, lowestPrice = 0, averagePrice = 0,
            isFavorite = false, url = "", imageUrl = ""
        )

        // Setup delete state
        viewModel.onAction(ListAction.OnSwipeDelete(product))

        viewModel.onAction(ListAction.OnDeleteConfirm)
        advanceUntilIdle()

        coVerify { deleteProductUseCase(product.id) }
        assertNull(viewModel.state.value.productToDelete)
        coVerify(atLeast = 2) { searchProductsUseCase(any()) }
    }

    @Test
    // 삭제 취소 시 삭제할 상품 초기화
    fun `OnDeleteCancel clears product to delete`() = runTest {
        coEvery { searchProductsUseCase(any()) } returns Result.Success(emptyList())
        initViewModel()
        advanceUntilIdle()

        val product = Product(
            id = 1, name = "Test", originalPrice = 0, currentPrice = 0,
            targetPrice = 0, lowestPrice = 0, averagePrice = 0,
            isFavorite = false, url = "", imageUrl = ""
        )

        viewModel.onAction(ListAction.OnSwipeDelete(product))
        viewModel.onAction(ListAction.OnDeleteCancel)

        assertNull(viewModel.state.value.productToDelete)
    }

    @Test
    // 공유 인텐트 핸들러가 상품 추가 트리거
    fun `ShareIntentHandler triggers add product`() = runTest {
        coEvery { searchProductsUseCase(any()) } returns Result.Success(emptyList())
        coEvery { addProductUseCase(any()) } returns Result.Success(Unit)

        val sharedUrl = "http://shared.com"
        ShareIntentHandler.emitUrl(sharedUrl)

        initViewModel()
        advanceUntilIdle()

        coVerify { addProductUseCase(sharedUrl) }

    }

    @Test
    // 에러 발생 시 토스트 메시지 표시
    fun `Error handling shows toast`() = runTest {
        coEvery { searchProductsUseCase(any()) } returns Result.Error(DataError.Network.UNKNOWN)

        initViewModel()

        viewModel.event.test {
            advanceUntilIdle()
            val event = awaitItem()
            assertTrue(event is ListEvent.ShowToast)
        }
    }

    @Test
    fun `shouldShowClipboardPrompt delegates to manager`() {
        initViewModel()
        val url = "http://test.com"
        every { clipboardStateManager.shouldShowSnackbar(url) } returns true

        assertTrue(viewModel.shouldShowClipboardPrompt(url))
        io.mockk.verify { clipboardStateManager.shouldShowSnackbar(url) }
    }

    @Test
    fun `setClipboardDismissed delegates to manager`() {
        initViewModel()
        val url = "http://test.com"
        viewModel.setClipboardDismissed(url)
        io.mockk.verify { clipboardStateManager.setDismissed(url) }
    }
}
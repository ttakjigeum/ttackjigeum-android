package com.devhjs.ttackjigeum_android.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhjs.ttackjigeum_android.core.manager.ClipboardStateManager
import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.core.util.ShareIntentHandler
import com.devhjs.ttackjigeum_android.domain.usecase.AddProductUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.DeleteProductUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.SearchProductsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListViewModel(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val clipboardStateManager: ClipboardStateManager
) : ViewModel() {

    private val _state = MutableStateFlow(ListState())
    val state: StateFlow<ListState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<ListEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            // 반응형 데이터 로딩
            _state
                .map { it.searchQuery }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    _state.update { it.copy(isLoading = true) }
                    searchProductsUseCase(query)
                }
                .collect { result ->
                     when (result) {
                        is Result.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    products = result.data,
                                )
                            }
                        }
                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false) }
                            _event.emit(ListEvent.ShowToast("상품 정보를 불러오는데 실패했습니다."))
                        }
                    }
                }
        }

        viewModelScope.launch {
             // 2. 공유 인텐트 대기 및 처리
            ShareIntentHandler.sharedUrl.collectLatest { url ->
                url?.let {
                    _state.update { it.copy(isLoading = true) }
                    handleOnAddLinkConfirm(it)
                    ShareIntentHandler.consumeUrl()
                }
            }
        }
    }

    fun onAction(action: ListAction) {
        when (action) {
            is ListAction.OnProductClick -> {
                viewModelScope.launch {
                    _event.emit(ListEvent.NavigateToDetail(action.product.id))
                }
            }
            is ListAction.OnNotificationClick -> {
                // 알림 화면 이동 로직 등 처리
            }
            is ListAction.OnAddLinkConfirm -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    handleOnAddLinkConfirm(action.link)
                }
            }
            is ListAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
            }
            is ListAction.OnSwipeDelete -> {
                _state.update { it.copy(productToDelete = action.product) }
            }
            is ListAction.OnDeleteConfirm -> {
                val product = _state.value.productToDelete
                if (product != null) {
                    viewModelScope.launch {
                        _state.update { it.copy(isLoading = true, productToDelete = null) }
                        deleteProduct(product.id)
                    }
                }
            }
            is ListAction.OnDeleteCancel -> {
                _state.update { it.copy(productToDelete = null) }
            }
        }
    }

    private suspend fun handleOnAddLinkConfirm(link: String) {
        when (val result = addProductUseCase(link)) {
            is Result.Success -> {
                // Flow를 통해 자동 업데이트됨
                clipboardStateManager.markUrlProcessed(link)
      
            }
            is Result.Error -> {
                _state.update { it.copy(isLoading = false) }
                val msg = when (result.error) {
                    DataError.Local.DUPLICATE -> "이미 등록된 상품입니다."
                    else -> "상품 등록에 실패했습니다."
                }
                _event.emit(ListEvent.ShowToast(msg))
            }
        }
    }

    private suspend fun deleteProduct(productId: Long) {
        when (deleteProductUseCase(productId)) {
            is Result.Success -> {
                // Flow를 통해 자동 업데이트됨
                _event.emit(ListEvent.ShowToast("상품이 삭제되었습니다."))
            }
            is Result.Error -> {
                _state.update { it.copy(isLoading = false) }
                _event.emit(ListEvent.ShowToast("상품 삭제에 실패했습니다."))
            }
        }
    }
    fun shouldShowClipboardPrompt(url: String): Boolean {
        return clipboardStateManager.shouldShowSnackbar(url)
    }

    fun setClipboardDismissed(url: String) {
        clipboardStateManager.setDismissed(url)
    }
}

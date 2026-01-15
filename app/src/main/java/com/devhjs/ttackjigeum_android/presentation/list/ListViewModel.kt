package com.devhjs.ttackjigeum_android.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.usecase.AddProductUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.SearchProductsUseCase
import com.devhjs.ttackjigeum_android.core.util.ShareIntentHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListViewModel(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val addProductUseCase: AddProductUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ListState())
    val state: StateFlow<ListState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<ListEvent>()
    val event = _event.asSharedFlow()

    init {
        loadProducts()
        viewModelScope.launch {
            ShareIntentHandler.sharedUrl.collectLatest { url ->
                onAction(ListAction.OnAddLinkConfirm(url))
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
                    when (val result = addProductUseCase(action.link)) {
                        is Result.Success -> {
                            loadProducts()
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
            }
            is ListAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
                loadProducts()
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (val result = searchProductsUseCase(_state.value.searchQuery)) {
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
}

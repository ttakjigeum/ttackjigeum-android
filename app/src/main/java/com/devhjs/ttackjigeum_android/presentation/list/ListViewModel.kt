package com.devhjs.ttackjigeum_android.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.usecase.SearchProductsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListViewModel(
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListState())
    val state: StateFlow<ListState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<ListEvent>()
    val event = _event.asSharedFlow()

    init {
        loadProducts()
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
                // 링크 추가 로직 처리 (TODO)
                loadProducts() // 리프레시 예시
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
                            products = result.data
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

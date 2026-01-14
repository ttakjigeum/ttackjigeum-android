package com.devhjs.ttackjigeum_android.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhjs.ttackjigeum_android.core.navigation.Route
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.usecase.GetPriceHistoryUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.GetProductUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val productId: Long,
    private val getProductUseCase: GetProductUseCase,
    private val getPriceHistoryUseCase: GetPriceHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState(isLoading = true))
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<DetailEvent>()
    val event = _event.asSharedFlow()

    init {
        loadDetailData()
    }

    private fun loadDetailData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val productResult = getProductUseCase(productId)
            val historyResult = getPriceHistoryUseCase(productId)

            if (productResult is Result.Success && historyResult is Result.Success) {
                val product = productResult.data
                _state.update {
                    it.copy(
                        isLoading = false,
                        product = product,
                        priceHistories = historyResult.data,
                        isNotificationActive = product.isFavorite
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
                // TODO: 에러 처리 로직 추가 (예: 에러 액션 발행)
            }
        }
    }

    fun onAction(action: DetailAction) {
        when (action) {
            is DetailAction.OnBackClick -> {
                viewModelScope.launch {
                    _event.emit(DetailEvent.NavigateBack)
                }
            }
            is DetailAction.OnNotificationToggle -> {
                _state.update { it.copy(isNotificationActive = !it.isNotificationActive) }
            }
            is DetailAction.OnPurchaseClick -> {
                _state.value.product?.url?.let { url ->
                    viewModelScope.launch {
                        _event.emit(DetailEvent.OpenUrl(url))
                    }
                }
            }
        }
    }
}

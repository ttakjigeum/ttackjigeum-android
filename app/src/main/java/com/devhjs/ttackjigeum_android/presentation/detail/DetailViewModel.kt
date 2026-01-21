package com.devhjs.ttackjigeum_android.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhjs.ttackjigeum_android.core.navigation.Route
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.usecase.GetPriceHistoryUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.GetProductUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.GetUserConfigUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.ToggleProductNotificationUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.UpdateTargetPriceUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val productId: Long,
    private val getProductUseCase: GetProductUseCase,
    private val getPriceHistoryUseCase: GetPriceHistoryUseCase,
    private val getUserConfigUseCase: GetUserConfigUseCase,
    private val toggleProductNotificationUseCase: ToggleProductNotificationUseCase,
    private val updateTargetPriceUseCase: UpdateTargetPriceUseCase
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
            val userConfig = getUserConfigUseCase(productId)

            if (productResult is Result.Success && historyResult is Result.Success) {
                val fetchedProduct = productResult.data
                val finalProduct = if (userConfig != null && userConfig.targetPrice > 0) {
                     fetchedProduct.copy(targetPrice = userConfig.targetPrice)
                } else {
                     fetchedProduct
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        product = finalProduct,
                        priceHistories = historyResult.data,
                        isNotificationActive = userConfig?.notificationEnabled ?: false
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
                viewModelScope.launch {
                    val currentProduct = _state.value.product ?: return@launch
                    toggleProductNotificationUseCase(currentProduct.id, currentProduct.targetPrice)
                    
                    // 토글 실행 완료, UI 상태 업데이트
                    val updatedConfig = getUserConfigUseCase(currentProduct.id)
                    _state.update { it.copy(isNotificationActive = updatedConfig?.notificationEnabled ?: false) }
                }
            }
            is DetailAction.OnPurchaseClick -> {
                _state.value.product?.url?.let { url ->
                    viewModelScope.launch {
                        _event.emit(DetailEvent.OpenUrl(url))
                    }
                }
            }
            is DetailAction.OnTargetPriceChange -> {
                viewModelScope.launch {
                    updateTargetPriceUseCase(productId, action.price)
                    // 낙관적 업데이트(Optimistic update) 또는 설정 리로드?
                    // 현재는 필요하다면 변경 사항을 즉시 반영하기 위해 로컬 상태의 상품 목표 가격을 업데이트합니다.
                    // 하지만 슬라이더가 대부분의 자체 상태를 보유합니다.
                    // 그럼에도 불구하고 동기화를 유지해야 합니다.
                     _state.update { currentState ->
                         currentState.copy(
                             product = currentState.product?.copy(targetPrice = action.price)
                         )
                     }
                }
            }
        }
    }
}

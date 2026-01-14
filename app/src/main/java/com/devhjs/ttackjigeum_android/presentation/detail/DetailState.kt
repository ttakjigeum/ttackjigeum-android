package com.devhjs.ttackjigeum_android.presentation.detail

import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.model.Product

data class DetailState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val priceHistories: List<PriceHistory> = emptyList(),
    val isNotificationActive: Boolean = false
)

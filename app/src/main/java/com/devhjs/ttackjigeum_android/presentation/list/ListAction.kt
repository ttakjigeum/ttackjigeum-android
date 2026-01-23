package com.devhjs.ttackjigeum_android.presentation.list

import com.devhjs.ttackjigeum_android.domain.model.Product

sealed interface ListAction {
    data class OnProductClick(val product: Product) : ListAction
    data object OnNotificationClick : ListAction
    data class OnAddLinkConfirm(val link: String) : ListAction
    data class OnSearchQueryChange(val query: String) : ListAction
    data class OnSwipeDelete(val product: Product) : ListAction
    data object OnDeleteConfirm : ListAction
    data object OnDeleteCancel : ListAction
}

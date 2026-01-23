package com.devhjs.ttackjigeum_android.presentation.list

sealed interface ListEvent {
    data class NavigateToDetail(val productId: Long) : ListEvent
    data class ShowToast(val message: String) : ListEvent
}

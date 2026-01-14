package com.devhjs.ttackjigeum_android.presentation.detail

sealed interface DetailAction {
    data object OnBackClick : DetailAction
    data object OnNotificationToggle : DetailAction
    data object OnPurchaseClick : DetailAction
}

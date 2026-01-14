package com.devhjs.ttackjigeum_android.presentation.detail

sealed interface DetailEvent {
    data object NavigateBack : DetailEvent
    data class OpenUrl(val url: String) : DetailEvent
}

package com.devhjs.ttackjigeum_android.core.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ShareIntentHandler {
    private val _sharedUrl = MutableStateFlow<String?>(null)
    val sharedUrl = _sharedUrl.asStateFlow()

    fun emitUrl(url: String) {
        _sharedUrl.value = url
    }

    fun consumeUrl() {
        _sharedUrl.value = null
    }
}

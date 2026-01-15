package com.devhjs.ttackjigeum_android.core.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ShareIntentHandler {
    private val _sharedUrl = MutableSharedFlow<String>(replay = 0)
    val sharedUrl = _sharedUrl.asSharedFlow()

    suspend fun emitUrl(url: String) {
        _sharedUrl.emit(url)
    }
}

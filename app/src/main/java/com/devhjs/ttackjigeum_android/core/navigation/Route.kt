package com.devhjs.ttackjigeum_android.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object List : Route

    @Serializable
    data class Detail(val productId: Long) : Route
}
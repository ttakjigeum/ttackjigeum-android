package com.devhjs.ttackjigeum_android.domain.model

data class Notification(
    val id: Long,
    val productId: Long,
    val message: String,
    val timestamp: String,
    val isRead: Boolean
)

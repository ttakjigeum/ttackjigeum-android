package com.devhjs.ttackjigeum_android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_configs")
data class UserConfigEntity(
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    val productId: Long,

    @ColumnInfo(name = "target_price")
    val targetPrice: Int,

    @ColumnInfo(name = "notification_enabled")
    val notificationEnabled: Boolean
)
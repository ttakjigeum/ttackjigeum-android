package com.devhjs.ttackjigeum_android.data.mapper

import com.devhjs.ttackjigeum_android.data.local.entity.UserConfigEntity
import com.devhjs.ttackjigeum_android.domain.model.UserConfig

fun UserConfigEntity.toDomain(): UserConfig {
    return UserConfig(
        productId = productId,
        targetPrice = targetPrice,
        notificationEnabled = notificationEnabled
    )
}

fun UserConfig.toEntity(): UserConfigEntity {
    return UserConfigEntity(
        productId = productId,
        targetPrice = targetPrice,
        notificationEnabled = notificationEnabled
    )
}

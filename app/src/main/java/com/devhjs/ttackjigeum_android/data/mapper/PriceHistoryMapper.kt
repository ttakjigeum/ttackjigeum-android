package com.devhjs.ttackjigeum_android.data.mapper

import com.devhjs.ttackjigeum_android.data.dto.PriceHistoryDto
import com.devhjs.ttackjigeum_android.domain.model.PriceHistory

fun PriceHistoryDto.toDomain(): PriceHistory {
    return PriceHistory(
        datetime = datetime ?: "",
        price = price ?: 0
    )
}

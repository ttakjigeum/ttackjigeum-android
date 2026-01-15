package com.devhjs.ttackjigeum_android.domain.parser

import com.devhjs.ttackjigeum_android.domain.model.ParsedProductData

interface ProductParser {
    suspend fun parseProduct(url: String): Result<ParsedProductData>
}

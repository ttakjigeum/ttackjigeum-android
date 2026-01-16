package com.devhjs.ttackjigeum_android.data.parser

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.data.repository.mock.MockData
import com.devhjs.ttackjigeum_android.domain.model.ParsedProductData
import com.devhjs.ttackjigeum_android.domain.parser.ProductParser

class DevProductParser : ProductParser {
    override suspend fun parseProduct(url: String): Result<ParsedProductData, DataError> {
        val mockProduct =
            MockData.MockProducts.find { it.url == url } ?: MockData.mockProduct

        return Result.Success(
            ParsedProductData(
                name = mockProduct.name,
                originalPrice = mockProduct.originalPrice,
                currentPrice = mockProduct.currentPrice,
                url = mockProduct.url,
                imageUrl = mockProduct.imageUrl,
            ),
        )
    }
}
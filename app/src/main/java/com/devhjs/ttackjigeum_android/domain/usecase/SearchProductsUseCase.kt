package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product

class SearchProductsUseCase(
    private val getProductsUseCase: GetProductsUseCase
) {
    suspend operator fun invoke(query: String): Result<List<Product>, DataError> {
        return when (val result = getProductsUseCase()) {
            is Result.Success -> {
                if (query.isBlank()) {
                    Result.Success(result.data)
                } else {
                    val filtered = result.data.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                    Result.Success(filtered)
                }
            }
            is Result.Error -> {
                result
            }
        }
    }
}

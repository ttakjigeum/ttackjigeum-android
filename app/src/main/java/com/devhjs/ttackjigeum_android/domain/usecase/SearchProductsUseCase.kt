package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchProductsUseCase(
    private val getProductsUseCase: GetProductsUseCase
) {
    operator fun invoke(query: String): Flow<Result<List<Product>, DataError>> {
        return getProductsUseCase().map { result ->
            when (result) {
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
}

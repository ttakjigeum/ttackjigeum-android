package com.devhjs.ttackjigeum_android.domain.usecase

import com.devhjs.ttackjigeum_android.domain.model.Product

class SearchProductsUseCase {
    operator fun invoke(products: List<Product>, query: String): List<Product> {
        if (query.isBlank()) {
            return products
        }
        return products.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }
}

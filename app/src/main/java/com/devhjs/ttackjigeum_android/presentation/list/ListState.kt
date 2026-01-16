package com.devhjs.ttackjigeum_android.presentation.list

import androidx.compose.runtime.Immutable
import com.devhjs.ttackjigeum_android.domain.model.Product

@Immutable
data class ListState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val productToDelete: Product? = null
)

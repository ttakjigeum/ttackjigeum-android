package com.devhjs.ttackjigeum_android.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay


import com.devhjs.ttackjigeum_android.presentation.list.ListRoot


@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(Route.List)


    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Route.List> {
                ListRoot(
                    navigateToDetail = { productId -> 
                        backStack.add(Route.Detail(productId)) 
                    }
                )
            }
            entry<Route.Detail> { route ->
                val productId = route.id
                // TODO: DetailScreen(productId = productId) 호출로 변경 필요
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = { backStack.removeAt(backStack.lastIndex) }) {
                        Text("Go Back (Product ID: $productId)")
                    }
                }
            }
        }
    )
}

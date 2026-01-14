package com.devhjs.ttackjigeum_android.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay


import com.devhjs.ttackjigeum_android.presentation.detail.DetailRoot
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
                DetailRoot(
                    id = route.productId,
                    onBackClick = { backStack.removeAt(backStack.lastIndex) }
                )
            }
        }
    )
}

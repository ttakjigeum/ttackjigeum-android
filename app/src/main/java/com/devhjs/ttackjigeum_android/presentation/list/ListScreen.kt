package com.devhjs.ttackjigeum_android.presentation.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devhjs.ttackjigeum_android.presentation.component.AddProductBottomSheet
import com.devhjs.ttackjigeum_android.presentation.component.CustomAppBar
import com.devhjs.ttackjigeum_android.presentation.component.CustomFloatingActionButton
import com.devhjs.ttackjigeum_android.presentation.component.ProductCardItem
import com.devhjs.ttackjigeum_android.presentation.component.SearchBar
import com.devhjs.ttackjigeum_android.ui.theme.AppColors
import com.devhjs.ttackjigeum_android.ui.theme.AppTextStyles

@Composable
fun ListScreen(
    state: ListState,
    onAction: (ListAction) -> Unit,
) {
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AppColors.AppBackground,
        topBar = {
            CustomAppBar(
                showBadge = true,
                onNotificationClick = { onAction(ListAction.OnNotificationClick) },
            )
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                onClick = { showSheet = true },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                value = state.searchQuery,
                onValueChange = { onAction(ListAction.OnSearchQueryChange(it)) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                if (!state.isLoading && state.products.isEmpty()) {
                    Text(
                        text = if (state.searchQuery.isEmpty()) "+ 를 눌러서 상품을 등록해보세요." else "검색 결과가 없습니다.",
                        color = AppColors.TextGray1,
                        style = AppTextStyles.mediumTextRegular,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = state.products,
                        key = { it.id },
                    ) { product ->
                        ProductCardItem(
                            product = product,
                            onClick = { onAction(ListAction.OnProductClick(product)) },
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppColors.Primary,
                    )
                }
            }
        }

        if (showSheet) {
            AddProductBottomSheet(
                onDismissRequest = { showSheet = false },
                onConfirm = { link ->
                    onAction(ListAction.OnAddLinkConfirm(link))
                    showSheet = false
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    ListScreen(
        state = ListState(),
        onAction = {},
    )
}

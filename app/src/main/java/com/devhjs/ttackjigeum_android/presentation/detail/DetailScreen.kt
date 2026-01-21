package com.devhjs.ttackjigeum_android.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devhjs.ttackjigeum_android.presentation.component.DetailBottomBar
import com.devhjs.ttackjigeum_android.presentation.component.DetailProductCard
import com.devhjs.ttackjigeum_android.presentation.component.DetailTopAppBar
import com.devhjs.ttackjigeum_android.presentation.component.HistoryGraph
import com.devhjs.ttackjigeum_android.presentation.component.LoadingCircleIndicator
import com.devhjs.ttackjigeum_android.presentation.component.PriceInfoCard
import com.devhjs.ttackjigeum_android.presentation.component.TargetPriceSlider
import com.devhjs.ttackjigeum_android.ui.theme.AppColors
import com.devhjs.ttackjigeum_android.ui.theme.AppTextStyles

@Composable
fun DetailScreen(
    state: DetailState,
    onAction: (DetailAction) -> Unit
) {
    Scaffold(
        topBar = {
            DetailTopAppBar(onBackClick = { onAction(DetailAction.OnBackClick) })
        },
        bottomBar = {
            DetailBottomBar(
                isNotificationActive = state.isNotificationActive,
                onNotificationClick = { onAction(DetailAction.OnNotificationToggle) },
                onPurchaseClick = { onAction(DetailAction.OnPurchaseClick) }
            )
        },
        containerColor = AppColors.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                LoadingCircleIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(AppColors.White)
                ) {
                    state.product?.let { product ->
                        DetailProductCard(
                            name = product.name,
                            imageUrl = product.imageUrl
                        )

                        Text(
                            text = product.name,
                            style = AppTextStyles.largeTextBold,
                            color = AppColors.Black,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PriceInfoCard(
                            currentPrice = product.currentPrice,
                            originalPrice = product.originalPrice.takeIf { it > product.currentPrice }, // 원가 검증
                            targetPrice = product.targetPrice,
                            lowestPrice = product.lowestPrice,
                            averagePrice = product.averagePrice
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    HistoryGraph(
                        histories = state.priceHistories
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    state.product?.let { product ->
                        TargetPriceSlider(
                            targetPrice = product.targetPrice,
                            currentPrice = product.currentPrice,
                            onTargetPriceChange = { onAction(DetailAction.OnTargetPriceChange(it)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp)) // 바텀 바를 위한 추가 여백 공간
                }
            }

        }
    }
}

@Preview
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        state = DetailState(
            isLoading = true,
            product = com.devhjs.ttackjigeum_android.domain.model.Product(
                id = 1,
                name = "Sony WH-1000XM5",
                originalPrice = 399000,
                currentPrice = 348000,
                targetPrice = 320000,
                lowestPrice = 298000,
                averagePrice = 356000,
                isFavorite = false,
                url = "",
                imageUrl = ""
            )
        ),
        onAction = {}
    )
}

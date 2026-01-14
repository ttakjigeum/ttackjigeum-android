package com.devhjs.ttackjigeum_android.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devhjs.ttackjigeum_android.ui.theme.AppColors
import com.devhjs.ttackjigeum_android.presentation.component.DetailBottomBar
import com.devhjs.ttackjigeum_android.presentation.component.DetailProductCard
import com.devhjs.ttackjigeum_android.presentation.component.DetailTopAppBar
import com.devhjs.ttackjigeum_android.presentation.component.HistoryGraph
import com.devhjs.ttackjigeum_android.presentation.component.PriceInfoCard
import com.devhjs.ttackjigeum_android.presentation.component.TargetPriceSlider

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(AppColors.White)
        ) {
            state.product?.let { product ->
                DetailProductCard(
                    name = product.name,
                    imageUrl = product.imageUrl
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                PriceInfoCard(
                    currentPrice = product.currentPrice,
                    originalPrice = product.originalPrice,
                    targetPrice = product.targetPrice
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            HistoryGraph(
                histories = state.priceHistories
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TargetPriceSlider()
            
            Spacer(modifier = Modifier.height(100.dp)) // Extra space for bottom bar
        }
    }
}

@Preview
@Composable
fun DetailScreenPreview() {
    DetailScreen()
}

package com.devhjs.ttackjigeum_android.presentation.list


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.devhjs.ttackjigeum_android.presentation.component.BadgeType
import com.devhjs.ttackjigeum_android.presentation.component.ProductCardItem

@Composable
fun ListScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        ProductCardItem(badgeType = BadgeType.LOWEST_PRICE)
        ProductCardItem(badgeType = BadgeType.PRICE_DROP)
        ProductCardItem(badgeType = BadgeType.NO_CHANGE)
    }
}

@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    ListScreen()
}
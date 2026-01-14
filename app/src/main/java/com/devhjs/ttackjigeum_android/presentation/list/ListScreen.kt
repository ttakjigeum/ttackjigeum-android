package com.devhjs.ttackjigeum_android.presentation.list


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devhjs.ttackjigeum_android.presentation.component.BadgeType
import com.devhjs.ttackjigeum_android.presentation.component.ProductCardItem
import com.devhjs.ttackjigeum_android.presentation.component.SearchBar

@Composable
fun ListScreen(modifier: Modifier = Modifier) {
    val searchText = remember { "" }

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        SearchBar(
            value = searchText,
            onValueChange = {},
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
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
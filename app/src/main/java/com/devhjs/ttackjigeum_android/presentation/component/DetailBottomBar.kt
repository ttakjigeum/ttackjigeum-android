package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devhjs.ttackjigeum_android.ui.theme.AppColors

@Composable
fun DetailBottomBar(
    isNotificationActive: Boolean,
    onNotificationClick: () -> Unit,
    onPurchaseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shadowElevation = 8.dp,
        color = AppColors.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NotificationButton(
                isActive = isNotificationActive,
                onClick = onNotificationClick
            )
            Spacer(modifier = Modifier.width(12.dp))
            PurchaseButton(
                onClick = onPurchaseClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview
@Composable
fun DetailBottomBarPreview() {
    DetailBottomBar(
        isNotificationActive = false,
        onNotificationClick = {},
        onPurchaseClick = {}
    )
}

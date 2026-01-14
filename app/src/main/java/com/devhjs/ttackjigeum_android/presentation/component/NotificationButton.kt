package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.ui.res.painterResource
import com.devhjs.ttackjigeum_android.R
import com.devhjs.ttackjigeum_android.ui.theme.AppColors

@Composable
fun NotificationButton(
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AppColors.TextGray2),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.size(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = AppColors.TextGray2
        )
    ) {
        Icon(
            painter = painterResource(id = if (isActive) R.drawable.ic_active else R.drawable.ic_paused),
            contentDescription = if (isActive) "알림 끄기" else "알림 켜기",
            tint = if (isActive) AppColors.Primary else AppColors.TextGray2 // Optional: Tint green when active
        )
    }
}

@Preview
@Composable
fun NotificationButtonPreview() {
    NotificationButton(isActive = true, onClick = {})
}

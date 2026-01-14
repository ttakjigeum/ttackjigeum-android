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

@Composable
fun NotificationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.size(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Gray
        )
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "알림 설정"
        )
    }
}

@Preview
@Composable
fun NotificationButtonPreview() {
    NotificationButton(onClick = {})
}

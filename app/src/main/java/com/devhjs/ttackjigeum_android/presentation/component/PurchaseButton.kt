package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devhjs.ttackjigeum_android.ui.theme.AppTextStyles

@Composable
fun PurchaseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00C853) // Green color
        )
    ) {
        Text(
            text = "지금 구매하기",
            style = AppTextStyles.mediumTextBold.copy(
                color = Color.White
            )
        )
        Spacer(modifier = Modifier.width(8.dp))

    }
}

@Preview
@Composable
fun PurchaseButtonPreview() {
    PurchaseButton(onClick = {})
}

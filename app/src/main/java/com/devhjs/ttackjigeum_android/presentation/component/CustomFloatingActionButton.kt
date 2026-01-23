package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.devhjs.ttackjigeum_android.R
import com.devhjs.ttackjigeum_android.ui.theme.AppColors

@Composable
fun CustomFloatingActionButton(
    onClick: () -> Unit = {},
) {
    FloatingActionButton(
        containerColor = AppColors.Primary,
        contentColor = AppColors.White,
        shape = CircleShape,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = "추가",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomFloatingActionButtonPreview() {
    CustomFloatingActionButton()
}
package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devhjs.ttackjigeum_android.ui.theme.AppColors
import com.devhjs.ttackjigeum_android.ui.theme.AppTextStyles

@Composable
fun DeleteConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = Color(0xFFFF5252), shape = CircleShape), // Red circle
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PriorityHigh, // Exclamation mark
                        contentDescription = "Warning",
                        tint = AppColors.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "상품 삭제",
                    style = AppTextStyles.largeTextBold,
                    color = AppColors.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = "정말 이 상품을 목록에서 삭제하시겠습니까?",
                    style = AppTextStyles.normalTextRegular,
                    color = AppColors.TextGray1,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Cancel Button
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.IconGray2,
                            contentColor = AppColors.TextGray1
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = null
                    ) {
                        Text(
                            text = "취소",
                            style = AppTextStyles.normalTextBold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Delete Button
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF5252), // Red
                            contentColor = AppColors.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = null
                    ) {
                        Text(
                            text = "삭제",
                            style = AppTextStyles.normalTextBold
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDeleteConfirmationDialog() {
    DeleteConfirmationDialog(
        onDismissRequest = {},
        onConfirm = {}
    )
}

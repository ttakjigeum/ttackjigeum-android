package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.painter.Painter
import com.devhjs.ttackjigeum_android.R
import com.devhjs.ttackjigeum_android.ui.theme.AppTextStyles
import com.devhjs.ttackjigeum_android.ui.theme.AppColors

@Composable
fun PriceInfoCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Price Row
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "₩348,000",
                style = AppTextStyles.headerTextBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "₩399,000",
                style = AppTextStyles.mediumTextRegular.copy(
                    color = AppColors.TextGray2,
                    textDecoration = TextDecoration.LineThrough
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check_circle),
                contentDescription = null,
                tint = AppColors.Primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "최근 30일 최저가",
                style = AppTextStyles.smallTextBold.copy(
                    color = AppColors.Primary
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PriceStatCard(
                modifier = Modifier.weight(1f),
                icon = painterResource(id = R.drawable.ic_check_circle),
                iconColor = AppColors.Primary,
                title = "역대 최저가",
                price = "₩298,000",
                date = "2026. 1. 14"
            )
            PriceStatCard(
                modifier = Modifier.weight(1f),
                icon = painterResource(id = R.drawable.ic_bar_chart),
                iconColor = AppColors.TextGray2,
                title = "평균가",
                price = "₩356,000",
                date = "최근 1개월"
            )
        }
    }
}

@Composable
fun PriceStatCard(
    modifier: Modifier = Modifier,
    icon: Painter,
    iconColor: Color,
    title: String,
    price: String,
    date: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.IconGray2),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        // border = BorderStroke(1.dp, Color(0xFFEEEEEE)) // Optional border
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = AppTextStyles.smallTextRegular.copy(
                        color = AppColors.TextGray1
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = price,
                style = AppTextStyles.largeTextBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date,
                style = AppTextStyles.smallTextRegular.copy(
                    color = AppColors.TextGray1
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PriceInfoCardPreview() {
    PriceInfoCard()
}

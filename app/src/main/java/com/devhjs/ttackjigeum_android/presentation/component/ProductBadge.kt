package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devhjs.ttackjigeum_android.R
import com.devhjs.ttackjigeum_android.ui.theme.AppColors

enum class BadgeType(
    val text: String,
    val textColor: Color,
    val backgroundColor: Color,
    val iconResId: Int? = null,
    val iconTint: Color = Color.Unspecified
) {
    LOWEST_PRICE(
        text = "최저가 달성",
        textColor = AppColors.Primary,
        backgroundColor = AppColors.Secondary,
        iconResId = R.drawable.ic_trending_down,
        iconTint = AppColors.Primary
    ),
    PRICE_DROP(
        text = "가격 하락",
        textColor = AppColors.Primary,
        backgroundColor = AppColors.Secondary,
        iconResId = R.drawable.ic_trending_down,
        iconTint = AppColors.Primary
    ),
    NO_CHANGE(
        text = "변동 없음",
        textColor = AppColors.TextGray1,
        backgroundColor = AppColors.IconGray2,
        iconResId = null,
        iconTint = Color.Unspecified
    )
}

@Composable
fun ProductBadge(
    type: BadgeType,
    modifier: Modifier = Modifier
) {
    Surface(
        color = type.backgroundColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (type.iconResId != null) {
                Icon(
                    painter = painterResource(type.iconResId),
                    contentDescription = null,
                    tint = type.iconTint,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = type.text,
                fontSize = 12.sp,
                color = type.textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, name = "Lowest Price")
@Composable
fun PreviewLowestPriceBadge() {
    ProductBadge(type = BadgeType.LOWEST_PRICE)
}

@Preview(showBackground = true, name = "Price Drop")
@Composable
fun PreviewPriceDropBadge() {
    ProductBadge(type = BadgeType.PRICE_DROP)
}

@Preview(showBackground = true, name = "No Change")
@Composable
fun PreviewNoChangeBadge() {
    ProductBadge(type = BadgeType.NO_CHANGE)
}

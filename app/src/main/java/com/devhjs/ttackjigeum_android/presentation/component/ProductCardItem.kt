package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devhjs.ttackjigeum_android.R
import com.devhjs.ttackjigeum_android.ui.theme.AppColors

@Composable
fun ProductCardItem(
    product: com.devhjs.ttackjigeum_android.domain.model.Product,
    onClick: () -> Unit = {},
) {
    val badgeType = when {
        product.currentPrice <= product.lowestPrice -> BadgeType.LOWEST_PRICE
        product.currentPrice < product.originalPrice -> BadgeType.PRICE_DROP
        else -> BadgeType.NO_CHANGE
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // 리스트 간격
        shape = RoundedCornerShape(16.dp), // 카드 둥근 모서리
        colors = CardDefaults.cardColors(containerColor = AppColors.White), // 배경 흰색
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // 살짝 그림자
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp) // 카드 내부 여백
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 좌측 상품 이미지 (이미지가 없을 때 ic_noimage 표시)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f) // 정사각형
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.IconGray2), // 배경색
                contentAlignment = Alignment.Center
            ) {
                // TODO: 실제 이미지 로딩 라이브러리(Coil 등) 사용 필요. 현재는 Mock 데이터 기반이므로 플레이스홀더 사용 혹은 이미지 URL 처리
                if (product.imageUrl.isNotEmpty()) {
                    // Coil 등을 사용하지 않는 환경이라면 플레이스홀더 유지, 추후 추가
                     Image(
                        painter = painterResource(R.drawable.ic_noimage),
                        contentDescription = "이미지 없음",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(AppColors.IconGray1)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_noimage),
                        contentDescription = "이미지 없음",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(AppColors.IconGray1)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. 우측 텍스트 영역
            Column(
                modifier = Modifier.weight(1f) // 남은 공간 다 차지하기
            ) {
                // (1) 상품명
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2, // 두 줄까지만 표시
                    overflow = TextOverflow.Ellipsis // 넘치면 ... 처리
                )

                Spacer(modifier = Modifier.height(4.dp))

                // (2) 목표가
                Text(
                    text = "목표가: ₩${java.text.NumberFormat.getInstance().format(product.targetPrice)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // (3) 가격 및 배지 Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween // 양쪽 끝 정렬
                ) {
                    // 현재 가격
                    Text(
                        text = "₩${java.text.NumberFormat.getInstance().format(product.currentPrice)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (badgeType == BadgeType.NO_CHANGE) AppColors.TextGray1 else AppColors.Primary
                    )

                    // 배지
                    ProductBadge(
                        type = badgeType
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductCardItem() {
    val mockProduct = com.devhjs.ttackjigeum_android.domain.model.Product(
        id = 1L,
        name = "소니 WH-1000XM5 무선 노이즈 캔셀링 헤드폰",
        originalPrice = 350000,
        currentPrice = 298000,
        targetPrice = 320000,
        lowestPrice = 290000,
        averagePrice = 330000,
        isFavorite = false,
        url = "",
        imageUrl = ""
    )
    ProductCardItem(product = mockProduct)
}
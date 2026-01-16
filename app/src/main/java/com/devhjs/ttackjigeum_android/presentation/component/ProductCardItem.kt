package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.devhjs.ttackjigeum_android.R
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCardItem(
    product: Product,
    onClick: () -> Unit = {},
    onSwipeDelete: () -> Unit = {},
) {
    val badgeType = when {
        product.currentPrice <= product.lowestPrice -> BadgeType.LOWEST_PRICE
        product.currentPrice < product.originalPrice -> BadgeType.PRICE_DROP
        product.currentPrice > product.originalPrice -> BadgeType.PRICE_RISE
        else -> BadgeType.NO_CHANGE
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onSwipeDelete()
                false
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = Color(0xFFDC2626)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        content = {
            Card(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp), // 카드 둥근 모서리
                colors = CardDefaults.cardColors(containerColor = AppColors.White), // 배경 흰색
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // 살짝 그림자
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp) // 카드 내부 여백
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 1. 좌측 상품 이미지 (Coil AsyncImage 사용)
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            // .aspectRatio(1f) // 정사각형 (size로 대체)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppColors.IconGray2), // 배경색
                        contentAlignment = Alignment.Center,
                    ) {
                        if (product.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                error = painterResource(R.drawable.ic_noimage),
                                placeholder = painterResource(R.drawable.ic_noimage),
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.ic_noimage),
                                contentDescription = "이미지 없음",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(AppColors.IconGray1),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // 2. 우측 텍스트 영역
                    Column(
                        modifier = Modifier.weight(1f), // 남은 공간 다 차지하기
                        verticalArrangement = Arrangement.Center,
                    ) {
                        // (1) 상품명
                        Text(
                            text = product.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1, // 한 줄까지만 표시
                            overflow = TextOverflow.Ellipsis, // 넘치면 ... 처리
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // (2) 목표가
                        Text(
                            text = "목표가: ₩${
                                java.text.NumberFormat.getInstance().format(product.targetPrice)
                            }",
                            fontSize = 14.sp,
                            color = Color.Gray,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // (3) 가격 및 배지 Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween, // 양쪽 끝 정렬
                        ) {
                            // 현재 가격
                            Text(
                                text = "₩${
                                    java.text.NumberFormat.getInstance().format(product.currentPrice)
                                }",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (badgeType == BadgeType.NO_CHANGE) AppColors.TextGray1 else AppColors.Primary,
                            )

                            // 배지
                            ProductBadge(
                                type = badgeType,
                            )
                        }
                    }
                }
            }
        },
        enableDismissFromStartToEnd = false
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewProductCardItem() {
    val mockProduct = Product(
        id = 1L,
        name = "소니 WH-1000XM5 무선 노이즈 캔셀링 헤드폰",
        originalPrice = 350000,
        currentPrice = 298000,
        targetPrice = 320000,
        lowestPrice = 290000,
        averagePrice = 330000,
        isFavorite = false,
        url = "",
        imageUrl = "",
    )
    ProductCardItem(product = mockProduct)
}

@Preview(showBackground = true)
@Composable
fun PreviewProductCardItemRise() {
    val mockProduct = Product(
        id = 2L,
        name = "가격 상승 예시 상품",
        originalPrice = 300000,
        currentPrice = 350000,
        targetPrice = 320000,
        lowestPrice = 290000,
        averagePrice = 330000,
        isFavorite = false,
        url = "",
        imageUrl = "",
    )
    ProductCardItem(product = mockProduct)
}
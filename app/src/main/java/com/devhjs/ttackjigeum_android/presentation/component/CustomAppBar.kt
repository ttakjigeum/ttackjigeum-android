package com.devhjs.ttackjigeum_android.presentation.component


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devhjs.ttackjigeum_android.R
import com.devhjs.ttackjigeum_android.ui.theme.AppColors
import com.devhjs.ttackjigeum_android.ui.theme.AppTextStyles

@Composable
fun CustomAppBar(
    showBadge: Boolean = false,
    onNotificationClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth() // 가로 꽉 채우기
            .height(56.dp)  // 일반적인 앱바 높이
            .padding(horizontal = 20.dp), // 좌우 여백
        verticalAlignment = Alignment.CenterVertically, // 세로 중앙 정렬
        horizontalArrangement = Arrangement.SpaceBetween, // 양쪽 끝으로 밀어내기
    ) {
        // 1. 왼쪽 타이틀 텍스트
        Text(
            text = "추적 중인 상품",
            style = AppTextStyles.largeTextBold.copy(
                color = AppColors.Black, fontSize = 25.sp,
            ),
        )

        // 2. 오른쪽 알림 아이콘 + 배지 (겹치기 위해 Box 사용)
        Box(
            contentAlignment = Alignment.TopEnd, // 내용물을 우상단 정렬
            modifier = Modifier.clickable { onNotificationClick() },
        ) {
            // (1) 종 아이콘
            Icon(
                painter = painterResource(R.drawable.ic_notification_filled),
                contentDescription = "알림",
                modifier = Modifier.size(28.dp),
                tint = AppColors.Black,
            )

            // (2) 초록색 점 (배지)
            if (showBadge) {
                Box(
                    modifier = Modifier
                        .padding(2.dp) // 아이콘 안쪽으로 살짝 들어오게
                        .size(10.dp)   // 점 크기
                        .clip(CircleShape) // 원형으로 자르기
                        .background(AppColors.Primary), // 초록색 (Material Green)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomAppBarPreview() {
    CustomAppBar(showBadge = true)
}
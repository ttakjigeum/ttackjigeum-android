package com.devhjs.ttackjigeum_android.presentation.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devhjs.ttackjigeum_android.ui.theme.AppColors
import com.devhjs.ttackjigeum_android.ui.theme.AppTextStyles

@Composable
fun CustomAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth() // 가로 꽉 채우기
            .statusBarsPadding() // 상태바 영역만큼 패딩 추가
            .height(56.dp)  // 일반적인 앱바 높이
            .padding(horizontal = 20.dp), // 좌우 여백
        verticalAlignment = Alignment.CenterVertically, // 세로 중앙 정렬
        horizontalArrangement = Arrangement.Center, // 양쪽 끝으로 밀어내기
    ) {
        Text(
            text = "관심 상품",
            style = AppTextStyles.largeTextBold.copy(
                color = AppColors.Black, fontSize = 25.sp,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomAppBarPreview() {
    CustomAppBar()
}
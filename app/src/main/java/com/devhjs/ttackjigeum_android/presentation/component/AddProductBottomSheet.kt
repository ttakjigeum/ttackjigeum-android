package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devhjs.ttackjigeum_android.R
import com.devhjs.ttackjigeum_android.ui.theme.AppColors
import com.devhjs.ttackjigeum_android.ui.theme.AppTextStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductBottomSheet(
    initialLink: String = "",
    onDismissRequest: () -> Unit = {},
    onConfirm: (String) -> Unit = {},
) {
    var linkText by remember { mutableStateOf(initialLink) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = AppColors.White,
        dragHandle = null, // 상단 핸들을 없애고 직접 헤더를 구현
    ) {
        AddProductBottomSheetContent(
            linkText = linkText,
            onLinkTextChange = { linkText = it },
            onDismissRequest = onDismissRequest,
            onConfirm = { onConfirm(linkText) },
        )
    }
}

@Composable
fun AddProductBottomSheetContent(
    linkText: String,
    onLinkTextChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .navigationBarsPadding(), // 키보드나 내비게이션 바 대응
    ) {
        // Header: 타이틀 및 닫기 버튼
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "상품 추가하기",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Black,
            )
            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "닫기",
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 중앙 입력 필드 (이미지 스타일)
        TextField(
            value = linkText,
            onValueChange = onLinkTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            placeholder = {
                Text(
                    text = "클릭하여 복사한 링크를 붙여넣으세요!",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = AppColors.TextGray2,
                    style = AppTextStyles.mediumTextRegular,
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AppColors.AppBackground,
                unfocusedContainerColor = AppColors.AppBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(32.dp),
            singleLine = true,
            textStyle = AppTextStyles.mediumTextRegular.copy(textAlign = TextAlign.Center),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 하단 확인 버튼
        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
            ),
        ) {
            Text(text = "확인", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddProductBottomSheetPreview() {
    AddProductBottomSheetContent(
        linkText = "",
        onLinkTextChange = {},
        onDismissRequest = {},
        onConfirm = {},
    )
}
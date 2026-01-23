package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devhjs.ttackjigeum_android.R
import com.devhjs.ttackjigeum_android.core.util.ClearFocusOnKeyboardDismissEffect
import com.devhjs.ttackjigeum_android.ui.theme.AppColors

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit = {},
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // 키보드 가시성 감지 및 포커스 해제
    ClearFocusOnKeyboardDismissEffect(isFocused = isFocused)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = AppColors.White,
                shape = RoundedCornerShape(16.dp), // 이미지와 같은 둥근 모서리
            )
            .border(
                width = 1.dp,
                color = if (isFocused) AppColors.Primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp), // 리스트 간격
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 돋보기 아이콘 (이미지의 녹색 계열 적용)
        Icon(
            painter = painterResource(id = R.drawable.ic_search), // 프로젝트의 검색 아이콘 리소스
            contentDescription = "Search",
            tint = AppColors.Primary,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 텍스트 입력 필드
        Box(contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(
                    text = "상품 검색...",
                    color = AppColors.TextGray1,
                    fontSize = 16.sp,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                    }
                )
            )
        }
    }
}

@Preview
@Composable
private fun SearchBarPreview() {
    var text by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .background(Color(0xFFF5F5F5))
            .padding(20.dp),
    ) {
        SearchBar(
            value = text,
            onValueChange = { text = it },
        )
    }
}

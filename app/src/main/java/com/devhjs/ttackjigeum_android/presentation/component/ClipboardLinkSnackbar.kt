package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devhjs.ttackjigeum_android.R
import com.devhjs.ttackjigeum_android.ui.theme.AppColors

@Composable
fun ClipboardLinkSnackbar(
    onPromptClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF222222), RoundedCornerShape(12.dp))
            .clickable { onPromptClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_link), // 해당 리소스가 존재하는지 확인하거나 기본 리소스를 사용하십시오.
                contentDescription = null,
                tint = AppColors.Blue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "복사한 링크를 관심상품으로 추가",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right), // 해당 리소스가 존재하는지 확인하십시오.
                contentDescription = null,
                tint = AppColors.Blue,
                modifier = Modifier.size(16.dp)
            )
        }
        
        IconButton(
            onClick = onDismissClick,
            modifier = Modifier.size(24.dp)
        ) {
             Icon(
                painter = painterResource(id = R.drawable.ic_close), // 해당 리소스가 존재하는지 확인하십시오.
                contentDescription = "닫기",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun ClipboardLinkSnackbarPreview() {
    ClipboardLinkSnackbar(onPromptClick = {}, onDismissClick = {})
}

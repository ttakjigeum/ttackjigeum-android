package com.devhjs.ttackjigeum_android.presentation.list

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.devhjs.ttackjigeum_android.presentation.component.CustomAppBar
import com.devhjs.ttackjigeum_android.presentation.component.CustomFloatingActionButton
import com.devhjs.ttackjigeum_android.presentation.list.component.AddProductBottomSheet
import com.devhjs.ttackjigeum_android.ui.theme.AppColors

@Composable
fun ListRoot(
    navigateToDetail: () -> Unit = {},
) {
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AppColors.AppBackground,
        topBar = {
            CustomAppBar(
                showBadge = true,
                onNotificationClick = navigateToDetail,
            )
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                onClick = { showSheet = true }
            )
        },
    ) { innerPadding ->
        ListScreen(modifier = Modifier.padding(innerPadding))

        if (showSheet) {
            AddProductBottomSheet(
                onDismissRequest = { showSheet = false },
                onConfirm = { link ->
                    // 여기서 링크 등록 로직 수행
                    showSheet = false
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListRootPreview() {
    ListRoot()
}
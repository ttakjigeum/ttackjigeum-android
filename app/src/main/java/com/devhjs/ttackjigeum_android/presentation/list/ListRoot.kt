package com.devhjs.ttackjigeum_android.presentation.list

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.devhjs.ttackjigeum_android.presentation.component.CustomAppBar
import com.devhjs.ttackjigeum_android.ui.theme.AppColors

@Composable
fun ListRoot(
    navigateToDetail: () -> Unit
) {
    Scaffold(
        containerColor = AppBackground,
        topBar = {
            CustomAppBar(
                showBadge = true,
                onNotificationClick = navigateToDetail
            )
        }
    ) { innerPadding ->
        ListScreen(modifier = Modifier.padding(innerPadding))
    }
}

package com.devhjs.ttackjigeum_android.presentation.list

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListRoot(
    viewModel: ListViewModel = koinViewModel(),
    navigateToDetail: (Long) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                is ListEvent.NavigateToDetail -> {
                    navigateToDetail(event.productId)
                }
                is ListEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ListScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

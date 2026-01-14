package com.devhjs.ttackjigeum_android.presentation.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailRoot(
    id: Long,
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = koinViewModel(parameters = { parametersOf(id) })
) {
    val state by viewModel.state.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(true) {
        viewModel.event.collect { event ->
            when (event) {
                is DetailEvent.NavigateBack -> onBackClick()
                is DetailEvent.OpenUrl -> {
                    uriHandler.openUri(event.url)
                }
            }
        }
    }

    DetailScreen(
        state = state,
        onAction = { action -> viewModel.onAction(action) }
    )
}

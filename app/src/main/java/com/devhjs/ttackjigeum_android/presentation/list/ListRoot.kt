package com.devhjs.ttackjigeum_android.presentation.list

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devhjs.ttackjigeum_android.core.util.ClipboardUtils
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListRoot(
    viewModel: ListViewModel = koinViewModel(),
    navigateToDetail: (Long) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var clipboardUrl by remember { mutableStateOf<String?>(null) }
    var showClipboardPrompt by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val url = ClipboardUtils.getClipboardUrl(context)
                if (url != null) {
                    clipboardUrl = url
                    showClipboardPrompt = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
        onAction = viewModel::onAction,
        clipboardUrl = clipboardUrl,
        showClipboardPrompt = showClipboardPrompt,
        onClipboardPromptClick = {
            showClipboardPrompt = false
        },
        onClipboardPromptDismiss = {
            showClipboardPrompt = false
        }
    )
}

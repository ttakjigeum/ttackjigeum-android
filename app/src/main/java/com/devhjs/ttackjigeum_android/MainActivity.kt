package com.devhjs.ttackjigeum_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.devhjs.ttackjigeum_android.presentation.MyApp
import androidx.lifecycle.lifecycleScope
import com.devhjs.ttackjigeum_android.core.util.ShareIntentHandler
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 99)
        }

        enableEdgeToEdge()
        setContent {
            MyApp()
        }

        // 초기 실행 시 공유 인텐트 처리
        handleShareIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // 앱이 실행 중일 때 공유 인텐트 처리
        handleShareIntent(intent)
    }

    private fun handleShareIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            sharedText?.let { text ->
                val url = extractUrl(text)
                url?.let {
                    Log.d("SHARE_INTENT", "추출된 URL: $it")
                    lifecycleScope.launch {
                        ShareIntentHandler.emitUrl(it)
                    }
                }
            }
        }
    }

    private fun extractUrl(text: String): String? {
        val urlRegex = "(https?://[\\w\\d:#@%/\\\$()~_?\\+-=\\\\\\.&]+)".toRegex()
        return urlRegex.find(text)?.value
    }
}

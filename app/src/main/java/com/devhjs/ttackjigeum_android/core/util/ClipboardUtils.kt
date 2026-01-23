package com.devhjs.ttackjigeum_android.core.util

import android.content.ClipboardManager
import android.content.Context

object ClipboardUtils {
    fun getClipboardUrl(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val item = clipboard.primaryClip?.getItemAt(0)
            val text = item?.coerceToText(context)?.toString()
            if (!text.isNullOrBlank()) {
                return extractUrl(text)
            }
        }
        return null
    }

    private fun extractUrl(text: String): String? {
        // MainActivity의 정규식과 동일하게 사용
        val urlRegex = "(https?://[\\w\\d:#@%/\\\$()~_?\\+-=\\\\\\.&]+)".toRegex()
        return urlRegex.find(text)?.value
    }
}

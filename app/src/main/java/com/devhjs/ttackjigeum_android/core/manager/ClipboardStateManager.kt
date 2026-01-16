package com.devhjs.ttackjigeum_android.core.manager

class ClipboardStateManager {

    private val dismissedUrls: MutableSet<String> = mutableSetOf()
    private val processedUrls: MutableSet<String> = mutableSetOf()

    fun shouldShowSnackbar(url: String): Boolean {
        return !dismissedUrls.contains(url) && !processedUrls.contains(url)
    }

    fun setDismissed(url: String) {
        dismissedUrls.add(url)
    }

    fun markUrlProcessed(url: String) {
        processedUrls.add(url)
    }
}

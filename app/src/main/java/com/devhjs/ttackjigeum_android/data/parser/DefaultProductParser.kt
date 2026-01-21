package com.devhjs.ttackjigeum_android.data.parser

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.ParsedProductData
import com.devhjs.ttackjigeum_android.domain.parser.ProductParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import android.net.Uri
import kotlin.coroutines.resume

class DefaultProductParser(private val context: Context) : ProductParser {

    override suspend fun parseProduct(url: String): Result<ParsedProductData, DataError> =
        withContext(Dispatchers.Main) {
            Log.d("ProductParser", "파싱 시작: $url")
            
            // 파싱 작업을 위한 Job 생성 (취소 처리를 위해)
            val extractionJob = Job()
            val extractionScope = CoroutineScope(Dispatchers.Main + extractionJob)

            return@withContext suspendCancellableCoroutine { continuation ->
                var isResumed = false

                try {
                    // applicationContext 사용으로 메모리 누수 방지
                    val webView = WebView(context.applicationContext).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            userAgentString = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36"
                            // 파싱 속도 향상을 위한 최적화
                            loadsImagesAutomatically = false
                            blockNetworkImage = true
                        }
                    }

                    var lastUrl: String? = null

                    webView.webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, pageUrl: String?) {
                            super.onPageFinished(view, pageUrl)

                            // URL이 실제로 변경되었을 때만 실행 (리다이렉트 대응)
                            if (lastUrl == pageUrl) {
                                return
                            }
                            lastUrl = pageUrl

                            // 페이지 로드 완료 후 리다이렉트 등을 고려하여 유효한 도메인이 될 때까지 대기
                            extractionScope.launch {
                                var redirectAttempts = 0
                                val maxRedirectAttempts = 20 // 0.5초 * 20 = 10초 대기

                                while (redirectAttempts < maxRedirectAttempts) {
                                    if (!isActive) return@launch

                                    // 현재 WebView의 최신 URL 확인
                                    val currentUrl = view?.url
                                    Log.d("ProductParser", "URL 감지 중 ($redirectAttempts): $currentUrl")

                                    val result = when {
                                        // 네이버 링크 브릿지 페이지 등은 리다이렉트 대기 (파싱하지 않음)
                                        currentUrl?.contains("link.naver.com") == true -> {
                                            Log.d("ProductParser", "네이버 리다이렉트 페이지 감지: $currentUrl")
                                            val uri = Uri.parse(currentUrl)
                                            val targetUrl = uri.getQueryParameter("url")
                                            
                                            if (!targetUrl.isNullOrEmpty()) {
                                                Log.d("ProductParser", "리다이렉트 대상 URL 추출 성공, 이동: $targetUrl")
                                                view?.loadUrl(targetUrl)
                                                return@launch
                                            }
                                            null
                                        }
                                        currentUrl?.contains("naver.com") == true -> {
                                            Log.d("ProductParser", "네이버 도메인 감지됨: $currentUrl")
                                            extractNaverData(webView, currentUrl)
                                        }
                                        currentUrl?.contains("coupang.com") == true -> {
                                            Log.d("ProductParser", "쿠팡 도메인 감지됨: $currentUrl")
                                            extractCoupangData(webView, currentUrl)
                                        }
                                        else -> null // 유효한 도메인 아님, 대기
                                    }

                                    if (result != null) {
                                        // 파싱 시도 결과가 나왔으므로 반환
                                        if (!isResumed && continuation.isActive) {
                                            isResumed = true
                                            safeDestroy(webView)
                                            continuation.resume(result)
                                        }
                                        return@launch
                                    }

                                    // 유효한 도메인이 아니면 잠시 대기
                                    delay(500)
                                    redirectAttempts++
                                }

                                // 타임아웃: 유효한 도메인을 찾지 못함
                                Log.e("ProductParser", "유효한 쇼핑몰 URL을 찾을 수 없음: ${view?.url}")
                                if (!isResumed && continuation.isActive) {
                                    isResumed = true
                                    safeDestroy(webView)
                                    continuation.resume(Result.Error(DataError.Local.UNKNOWN))
                                }
                            }
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?,
                        ) {
                            super.onReceivedError(view, errorCode, description, failingUrl)
                            Log.e("ProductParser", "onReceivedError - errorCode: $errorCode")
                            Log.e("ProductParser", "onReceivedError - description: $description")

                            // 앱 스킴 리다이렉트 에러(-10: ERR_UNKNOWN_URL_SCHEME)는 무시하고 진행
                            if (errorCode == -10) {
                                Log.w("ProductParser", "알 수 없는 URL 스킴 에러 무시 (앱 실행 시도 추정)")
                                return
                            }
                            
                            if (!isResumed && continuation.isActive) {
                                isResumed = true
                                safeDestroy(webView)
                                continuation.resume(Result.Error(DataError.Network.UNKNOWN))
                            }
                        }
                    }

                    webView.loadUrl(url)

                    continuation.invokeOnCancellation {
                        Log.d("ProductParser", "코루틴 취소됨, WebView 정리")
                        extractionJob.cancel()
                        safeDestroy(webView)
                    }
                } catch (e: Exception) {
                    Log.e("ProductParser", "예외 발생: ${e.message}", e)
                    if (!isResumed && continuation.isActive) {
                        isResumed = true
                        continuation.resume(Result.Error(DataError.Network.UNKNOWN))
                    }
                }
            }
        }

    private suspend fun extractNaverData(
        webView: WebView,
        url: String
    ): Result<ParsedProductData, DataError> {
        val maxAttempts = 20
        var attemptCount = 0

        while (attemptCount < maxAttempts) {
            attemptCount++
            
            val result = webView.evaluateJavascriptSuspend(
                """
                (function() {
                    try {
                        const name = document.querySelector('h3')?.textContent?.trim() || '';
                        
                        // 여러 셀렉터 시도 (클래스명 변경 대비)
                        let currentPrice = 0;
                        const priceElement = document.querySelector('.ODgtfJDtRT') || 
                                           document.querySelector('[class*="Price_price"]') || 
                                           document.querySelector('.price');
                        
                        if (priceElement) {
                            const priceText = priceElement.textContent.replace(/,/g, '');
                            currentPrice = parseInt(priceText);
                        }
                        
                        const imageUrl = document.querySelector('meta[property="og:image"]')?.content || '';
                        
                        return JSON.stringify({
                            name: name,
                            originalPrice: currentPrice,
                            currentPrice: currentPrice,
                            imageUrl: imageUrl,
                            foundPrice: currentPrice > 0
                        });
                    } catch(e) {
                        return JSON.stringify({ 
                            name: '',
                            originalPrice: 0,
                            currentPrice: 0,
                            imageUrl: '',
                            foundPrice: false,
                            error: e.message 
                        });
                    }
                })()
                """.trimIndent()
            )

            try {
                val jsonResult = result.removeSurrounding("\"").replace("\\\"", "\"")
                val jsonObject = JSONObject(jsonResult)

                val foundPrice = jsonObject.optBoolean("foundPrice", false)
                val name = jsonObject.optString("name", "")

                Log.d(
                    "ProductParser",
                    "Attempt $attemptCount/$maxAttempts: foundPrice=$foundPrice, name=$name",
                )

                if (foundPrice && name.isNotEmpty()) {
                    val productData = ParsedProductData(
                        name = name,
                        originalPrice = jsonObject.optInt("originalPrice", 0),
                        currentPrice = jsonObject.optInt("currentPrice", 0),
                        imageUrl = jsonObject.optString("imageUrl", ""),
                        url = url,
                    )
                    Log.d("ProductParser", "✅ 파싱 성공: $productData")
                    return Result.Success(productData)
                }
            } catch (e: Exception) {
                Log.e("ProductParser", "파싱 오류", e)
            }

            // 실패 시 대기 후 재시도
            delay(500)
        }

        Log.e("ProductParser", "❌ 최대 시도 횟수 초과")
        return Result.Error(DataError.Local.UNKNOWN)
    }

    private suspend fun extractCoupangData(
        webView: WebView,
        url: String
    ): Result<ParsedProductData, DataError> {
        val maxAttempts = 20
        var attemptCount = 0

        while (attemptCount < maxAttempts) {
            attemptCount++

            val result = webView.evaluateJavascriptSuspend(
                """
                (function() {
                    try {
                        let name = document.querySelector('h1')?.textContent?.trim() || 
                                   document.querySelector('meta[property="og:title"]')?.content || '';
                        name = name.replace(' - 쿠팡', '').replace(' | 쿠팡', '').trim();
                        
                        let currentPrice = 0;
                        const currentPriceElem = document.querySelector('div.price-amount.final-price-amount');
                        if (currentPriceElem) {
                            const text = currentPriceElem.textContent.replace(/[^0-9]/g, '');
                            if (text) {
                                currentPrice = parseInt(text);
                            }
                        }
                        
                        let originalPrice = currentPrice;
                        const originalPriceElem = document.querySelector('div.price-amount.original-price-amount');
                        if (originalPriceElem) {
                            const text = originalPriceElem.textContent.replace(/[^0-9]/g, '');
                            if (text && parseInt(text) > 0) {
                                originalPrice = parseInt(text);
                            }
                        }
                        
                        let imageUrl = document.querySelector('meta[property="og:image"]')?.content || '';
                        if (imageUrl.startsWith('//')) {
                            imageUrl = 'https:' + imageUrl;
                        }
                        
                        return JSON.stringify({
                            name: name,
                            originalPrice: originalPrice,
                            currentPrice: currentPrice,
                            imageUrl: imageUrl,
                            foundPrice: currentPrice > 0
                        });
                    } catch(e) {
                        return JSON.stringify({ error: e.message });
                    }
                })()
                """.trimIndent()
            )

            Log.d("ProductParser", "쿠팡 시도 $attemptCount: $result")

            try {
                val cleanJson = result.trim('"')
                    .replace("\\\"", "\"")
                    .replace("\\n", "")
                    .replace("\\t", "")

                val data = JSONObject(cleanJson)
                val foundPrice = data.optBoolean("foundPrice", false)

                if (foundPrice) {
                    val parsedData = ParsedProductData(
                        name = data.getString("name"),
                        originalPrice = data.getInt("originalPrice"),
                        currentPrice = data.getInt("currentPrice"),
                        url = url,
                        imageUrl = data.getString("imageUrl"),
                    )
                    Log.d("ProductParser", "쿠팡 파싱 완료 (시도: $attemptCount)")
                    return Result.Success(parsedData)
                }
            } catch (e: Exception) {
                Log.e("ProductParser", "체크 중 에러: ${e.message}")
            }
            
            delay(200)
        }
        
        // 쿠팡은 마지막에 한 번 더 시도하거나 실패 처리... 원래 로직은 parseJsonResult를 호출했지만
        // 여기서는 그냥 타임아웃 처리
        return Result.Error(DataError.Local.UNKNOWN)
    }

    private suspend fun WebView.evaluateJavascriptSuspend(script: String): String =
        suspendCancellableCoroutine { continuation ->
            this.evaluateJavascript(script) { result ->
                continuation.resume(result)
            }
        }

    // WebView 안전하게 정리하는 헬퍼 함수
    private fun safeDestroy(webView: WebView?) {
        try {
            webView?.apply {
                stopLoading()
                destroy()
            }
            Log.d("ProductParser", "WebView 정리 완료")
        } catch (e: Exception) {
            Log.e("ProductParser", "WebView 정리 실패", e)
        }
    }
}
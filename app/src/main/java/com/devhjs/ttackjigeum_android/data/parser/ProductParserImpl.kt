package com.devhjs.ttackjigeum_android.data.parser

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.devhjs.ttackjigeum_android.core.util.DataError
import com.devhjs.ttackjigeum_android.core.util.Result
import com.devhjs.ttackjigeum_android.domain.model.ParsedProductData
import com.devhjs.ttackjigeum_android.domain.parser.ProductParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.coroutines.resume

class ProductParserImpl(private val context: Context) : ProductParser {

    private val handler = Handler(Looper.getMainLooper())

    override suspend fun parseProduct(url: String): Result<ParsedProductData, DataError> =
        withContext(Dispatchers.Main) {
            Log.d("ProductParser", "파싱 시작: $url")
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
                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?,
                        ) {
                            super.onPageStarted(view, url, favicon)
                        }

                        override fun onPageFinished(view: WebView?, pageUrl: String?) {
                            super.onPageFinished(view, pageUrl)

                            // URL이 실제로 변경되었을 때만 실행 (리다이렉트 대응)
                            if (lastUrl == pageUrl) {
                                return
                            }
                            lastUrl = pageUrl

                            handler.postDelayed(
                                {
                                    when {
                                        pageUrl?.contains("naver.com") == true -> {
                                            Log.d("ProductParser", "네이버 파싱 시작")
                                            extractNaverData(webView, pageUrl) { result ->
                                                if (!isResumed) {
                                                    isResumed = true
                                                    continuation.resume(result)
                                                }
                                            }
                                        }
                                        pageUrl?.contains("coupang.com") == true -> {
                                            Log.d("ProductParser", "쿠팡 파싱 시작")
                                            extractCoupangData(webView, pageUrl) { result ->
                                                if (!isResumed) {
                                                    isResumed = true
                                                    continuation.resume(result)
                                                }
                                            }
                                        }
                                        else -> {
                                            Log.e("ProductParser", "지원하지 않는 URL: $pageUrl")
                                            safeDestroy(webView)
                                            if (!isResumed) {
                                                isResumed = true
                                                continuation.resume(
                                                    Result.Error(
                                                        DataError.Local.UNKNOWN,
                                                    ),
                                                )
                                            }
                                        }
                                    }
                                },
                                2000,
                            )
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
                            Log.e("ProductParser", "onReceivedError - failingUrl: $failingUrl")
                            safeDestroy(webView)
                            if (!isResumed) {
                                isResumed = true
                                continuation.resume(Result.Error(DataError.Network.UNKNOWN))
                            }
                        }
                    }

                    webView.loadUrl(url)

                    continuation.invokeOnCancellation {
                        Log.d("ProductParser", "코루틴 취소됨, WebView 정리")
                        handler.post {
                            safeDestroy(webView)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ProductParser", "예외 발생: ${e.message}", e)
                    if (!isResumed) {
                        isResumed = true
                        continuation.resume(Result.Error(DataError.Network.UNKNOWN))
                    }
                }
            }
        }

    private fun extractNaverData(
        webView: WebView,
        url: String,
        onComplete: (Result<ParsedProductData, DataError>) -> Unit,
    ) {
        var attemptCount = 0
        val maxAttempts = 20

        val checkPrice = object : Runnable {
            override fun run() {
                attemptCount++

                webView.evaluateJavascript(
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
                """.trimIndent(),
                ) { result ->
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

                            // 성공 시에도 WebView 정리
                            handler.post {
                                safeDestroy(webView)
                            }

                            onComplete(Result.Success(productData))
                        } else if (attemptCount < maxAttempts) {
                            handler.postDelayed(this, 500)
                        } else {
                            Log.e("ProductParser", "❌ 최대 시도 횟수 초과")

                            // 실패 시에도 WebView 정리
                            handler.post {
                                safeDestroy(webView)
                            }

                            onComplete(Result.Error(DataError.Local.UNKNOWN))
                        }
                    } catch (e: Exception) {
                        Log.e("ProductParser", "파싱 오류", e)
                        if (attemptCount < maxAttempts) {
                            handler.postDelayed(this, 500)
                        } else {
                            handler.post {
                                safeDestroy(webView)
                            }
                            onComplete(Result.Error(DataError.Local.UNKNOWN))
                        }
                    }
                }
            }
        }

        handler.postDelayed(checkPrice, 1000)
    }

    private fun extractCoupangData(
        webView: WebView,
        url: String,
        onComplete: (Result<ParsedProductData, DataError>) -> Unit,
    ) {
        handler.postDelayed(
            {
                var attemptCount = 0
                val maxAttempts = 20

                val checkPrice = object : Runnable {
                    override fun run() {
                        attemptCount++

                        webView.evaluateJavascript(
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
                        """.trimIndent(),
                        ) { result ->
                            Log.d("ProductParser", "쿠팡 시도 $attemptCount: $result")

                            try {
                                val cleanJson = result.trim('"')
                                    .replace("\\\"", "\"")
                                    .replace("\\n", "")
                                    .replace("\\t", "")

                                val data = org.json.JSONObject(cleanJson)
                                val foundPrice = data.optBoolean("foundPrice", false)

                                if (foundPrice || attemptCount >= maxAttempts) {
                                    Log.d(
                                        "ProductParser",
                                        "쿠팡 파싱 완료 (시도: $attemptCount, 가격찾음: $foundPrice)",
                                    )
                                    handler.post {
                                        safeDestroy(webView)
                                    }
                                    parseJsonResult(result, url, onComplete)
                                } else {
                                    handler.postDelayed(this, 200)
                                }
                            } catch (e: Exception) {
                                Log.e("ProductParser", "체크 중 에러: ${e.message}")
                                if (attemptCount >= maxAttempts) {
                                    handler.post {
                                        safeDestroy(webView)
                                    }
                                    parseJsonResult(result, url, onComplete)
                                } else {
                                    handler.postDelayed(this, 200)
                                }
                            }
                        }
                    }
                }

                handler.post(checkPrice)
            },
            1000,
        )
    }

    private fun parseJsonResult(
        jsonResult: String,
        url: String,
        onComplete: (Result<ParsedProductData, DataError>) -> Unit,
    ) {
        try {
            val cleanJson = jsonResult.trim('"')
                .replace("\\\"", "\"")
                .replace("\\n", "")
                .replace("\\t", "")

            Log.d("ProductParser", "파싱 중: $cleanJson")

            val data = org.json.JSONObject(cleanJson)

            // 디버그 정보 출력
            if (data.has("debug")) {
                Log.d("ProductParser", "Debug Info: ${data.getJSONObject("debug")}")
            }

            if (data.has("error")) {
                Log.e("ProductParser", "파싱 에러: ${data.getString("error")}")
                if (data.has("stack")) {
                    Log.e("ProductParser", "Stack: ${data.getString("stack")}")
                }
                onComplete(Result.Error(DataError.Local.UNKNOWN))
                return
            }

            val parsedData = ParsedProductData(
                name = data.getString("name"),
                originalPrice = data.getInt("originalPrice"),
                currentPrice = data.getInt("currentPrice"),
                url = url,
                imageUrl = data.getString("imageUrl"),
            )

            Log.d(
                "ProductParser",
                "파싱 완료 - 상품명: ${parsedData.name}, 현재가: ${parsedData.currentPrice}, 원가: ${parsedData.originalPrice}",
            )

            onComplete(Result.Success(parsedData))
        } catch (e: Exception) {
            Log.e("ProductParser", "parseJsonResult 예외 발생: ${e.message}", e)
            onComplete(Result.Error(DataError.Local.UNKNOWN))
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
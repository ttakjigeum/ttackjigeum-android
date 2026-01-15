package com.devhjs.ttackjigeum_android.data.parser

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.devhjs.ttackjigeum_android.domain.parser.ProductParser
import com.devhjs.ttackjigeum_android.domain.model.ParsedProductData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class ProductParserImpl(private val context: Context) : ProductParser {

    private val handler = Handler(Looper.getMainLooper())

    override suspend fun parseProduct(url: String): Result<ParsedProductData> =
        withContext(Dispatchers.Main) {
            return@withContext suspendCancellableCoroutine { continuation ->
                try {
                    val webView = WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.userAgentString =
                            "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36"
                    }

                    var isPageFinishedOnce = false

                    webView.webViewClient = object : WebViewClient() {
                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: android.graphics.Bitmap?,
                        ) {
                            super.onPageStarted(view, url, favicon)
                        }

                        override fun onPageFinished(view: WebView?, pageUrl: String?) {
                            super.onPageFinished(view, pageUrl)

                            // 첫 번째 onPageFinished만 처리
                            if (isPageFinishedOnce) {
                                return
                            }
                            isPageFinishedOnce = true

                            handler.postDelayed(
                                {
                                    when {
                                        pageUrl?.contains("naver.com") == true &&
                                                (pageUrl.contains("smartstore") || pageUrl.contains(
                                                    "brand",
                                                )) -> {
                                            extractNaverData(webView, pageUrl, continuation)
                                        }
                                        pageUrl?.contains("coupang.com") == true -> {
                                            extractCoupangData(webView, pageUrl, continuation)
                                        }
                                        else -> {
                                            Log.e("ProductParser", "지원하지 않는 URL: $pageUrl")
                                            webView.destroy()
                                            continuation.resume(
                                                Result.failure(
                                                    IllegalArgumentException("지원하지 않는 URL"),
                                                ),
                                            )
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
                            webView.destroy()
                            continuation.resume(Result.failure(Exception("WebView 로딩 실패: $description")))
                        }
                    }

                    webView.loadUrl(url)

                    continuation.invokeOnCancellation {
                        webView.destroy()
                    }
                } catch (e: Exception) {
                    Log.e("ProductParser", "예외 발생: ${e.message}", e)
                    continuation.resume(Result.failure(e))
                }
            }
        }

    private fun extractNaverData(
        webView: WebView,
        url: String,
        continuation: kotlin.coroutines.Continuation<Result<ParsedProductData>>,
    ) {
        webView.evaluateJavascript(
            """
            (function() {
                try {
                    const name = document.querySelector('h3')?.textContent?.trim() || '';
                    
                    let currentPrice = 0;
                    const allStrongs = Array.from(document.querySelectorAll('strong'));
                    const priceStrong = allStrongs.find(el => {
                        const text = el.textContent;
                        return text.includes('상품 가격') && /\d{1,3}(,\d{3})*원/.test(text);
                    });
                    
                    if (priceStrong) {
                        const match = priceStrong.textContent.match(/(\d{1,3}(?:,\d{3})*)원/);
                        if (match) {
                            currentPrice = parseInt(match[1].replace(/,/g, ''));
                        }
                    }
                    
                    if (currentPrice === 0) {
                        const prices = allStrongs
                            .map(el => {
                                const match = el.textContent.match(/(\d{1,3}(?:,\d{3})*)원/);
                                return match ? parseInt(match[1].replace(/,/g, '')) : 0;
                            })
                            .filter(price => price > 0 && price < 10000000);
                        
                        if (prices.length > 0) {
                            currentPrice = Math.max(...prices);
                        }
                    }
                    
                    let originalPrice = currentPrice;
                    const delElem = document.querySelector('del');
                    if (delElem) {
                        const match = delElem.textContent.match(/(\d{1,3}(?:,\d{3})*)원/);
                        if (match) {
                            originalPrice = parseInt(match[1].replace(/,/g, ''));
                        }
                    } else {
                        const originalStrong = allStrongs.find(el => el.textContent.includes('할인 전 가격'));
                        if (originalStrong) {
                            const match = originalStrong.textContent.match(/(\d{1,3}(?:,\d{3})*)원/);
                            if (match) {
                                originalPrice = parseInt(match[1].replace(/,/g, ''));
                            }
                        }
                    }
                    
                    const imageUrl = document.querySelector('meta[property="og:image"]')?.content || '';
                    
                    return JSON.stringify({
                        name: name,
                        originalPrice: originalPrice,
                        currentPrice: currentPrice,
                        imageUrl: imageUrl
                    });
                } catch(e) {
                    return JSON.stringify({ error: e.message });
                }
            })()
            """.trimIndent(),
        ) { result ->
            webView.destroy()
            parseJsonResult(result, url, continuation)
        }
    }

    private fun extractCoupangData(
        webView: WebView,
        url: String,
        continuation: kotlin.coroutines.Continuation<Result<ParsedProductData>>,
    ) {
        // 페이지가 완전히 로드될 때까지 추가 대기
        handler.postDelayed(
            {
                webView.evaluateJavascript(
                    """
            (function() {
                try {
                    // 디버그: 페이지 상태 확인
                    const debugInfo = {
                        hasH1: !!document.querySelector('h1'),
                        hasFinalPrice: !!document.querySelector('div.price-amount.final-price-amount'),
                        hasOriginalPrice: !!document.querySelector('div.price-amount.original-price-amount'),
                        bodyLength: document.body?.innerHTML?.length || 0
                    };
                    
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
                        debug: debugInfo
                    });
                } catch(e) {
                    return JSON.stringify({ error: e.message, stack: e.stack });
                }
            })()
            """.trimIndent(),
                ) { result ->
                    webView.destroy()
                    parseJsonResult(result, url, continuation)
                }
            },
            1000,
        )
    }

    private fun parseJsonResult(
        jsonResult: String,
        url: String,
        continuation: kotlin.coroutines.Continuation<Result<ParsedProductData>>,
    ) {
        try {
            val cleanJson = jsonResult.trim('"')
                .replace("\\\"", "\"")
                .replace("\\n", "")
                .replace("\\t", "")

            val data = org.json.JSONObject(cleanJson)

            if (data.has("error")) {
                Log.e("ProductParser", "파싱 에러: ${data.getString("error")}")
                if (data.has("stack")) {
                    Log.e("ProductParser", "Stack: ${data.getString("stack")}")
                }
                continuation.resume(Result.failure(Exception(data.getString("error"))))
                return
            }

            val parsedData = ParsedProductData(
                name = data.getString("name"),
                originalPrice = data.getInt("originalPrice"),
                currentPrice = data.getInt("currentPrice"),
                url = url,
                imageUrl = data.getString("imageUrl"),
            )

            continuation.resume(Result.success(parsedData))
        } catch (e: Exception) {
            Log.e("ProductParser", "parseJsonResult 예외 발생: ${e.message}", e)
            continuation.resume(Result.failure(e))
        }
    }
}

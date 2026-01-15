package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.ui.theme.AppColors
import com.devhjs.ttackjigeum_android.ui.theme.AppTextStyles
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HistoryGraph(
    modifier: Modifier = Modifier,
    histories: List<PriceHistory>
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "가격 추이",
                style = AppTextStyles.mediumTextBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            val isEmpty = histories.isEmpty()
            
            // Sort histories by date to ensure correct timeline order
            val sortedHistories = remember(histories) {
                histories.sortedBy { it.datetime }
            }
            
            // State for interactive tooltip
            var selectedIndex by remember { mutableStateOf<Int?>(null) }
            val textMeasurer = rememberTextMeasurer()
            val scrollState = rememberScrollState()

            // Auto-scroll to the end (most recent date) when data loads
            LaunchedEffect(sortedHistories) {
                if (sortedHistories.isNotEmpty()) {
                    scrollState.scrollTo(scrollState.maxValue)
                }
            }

            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val screenWidth = maxWidth
                
                if (isEmpty) {
                    Box(
                         modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Empty state mock graph (static)
                         GraphCanvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .alpha(0.3f),
                            histories = emptyList(), // Will trigger mock data inside
                             selectedIndex = null,
                             onPointSelected = {},
                             textMeasurer = textMeasurer
                        )
                        
                        Text(
                            text = "해당 상품은 딱지금에 완전 처음 추가된 상품이라\n가격 그래프가 존재하지 않아요!\n내일부터 가격 그래프를 그려드릴게요!",
                            style = AppTextStyles.smallTextRegular,
                            textAlign = TextAlign.Center,
                            color = AppColors.Black
                        )
                    }
                } else {
                    // Calculate required width based on item count
                    // Ensure at least screen width, otherwise expand
                    val itemWidth = 60.dp
                    val calculatedWidth = max(screenWidth, itemWidth * sortedHistories.size)
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                    ) {
                        GraphCanvas(
                            modifier = Modifier
                                .width(calculatedWidth)
                                .height(250.dp),
                            histories = sortedHistories,
                            selectedIndex = selectedIndex,
                            onPointSelected = { selectedIndex = it },
                            textMeasurer = textMeasurer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GraphCanvas(
    modifier: Modifier = Modifier,
    histories: List<PriceHistory>,
    selectedIndex: Int?,
    onPointSelected: (Int?) -> Unit,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val graphColor = AppColors.Primary
    val tooltipColor = Color(0xFF424242) // Dark Gray for tooltip background
    
    // safe parsing of points
    val pointsData = remember(histories) {
        if (histories.isEmpty()) {
            // Mock data for empty state background
            listOf(0.5f, 0.6f, 0.4f, 0.7f, 0.5f)
        } else {
            val maxPrice = histories.maxOfOrNull { it.price } ?: 1
            val minPrice = histories.minOfOrNull { it.price } ?: 0
            val range = (maxPrice - minPrice).takeIf { it > 0 } ?: 1
            histories.map { (it.price - minPrice).toFloat() / range }
        }
    }

    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { offset ->
                    if (histories.isNotEmpty()) {
                        val width = size.width
                        val stepX = width / (histories.size - 1).coerceAtLeast(1)
                        
                        // Find closest index
                        val index = (offset.x / stepX).roundToInt().coerceIn(0, histories.size - 1)
                        onPointSelected(index)
                    }
                }
            )
        }
    ) {
        val width = size.width
        val height = size.height
        
        // Define drawing area (padding for labels at bottom, tooltip at top)
        val bottomPadding = 40.dp.toPx()
        val topPadding = 60.dp.toPx()
        val graphHeight = height - bottomPadding - topPadding
        
        if (pointsData.isEmpty()) return@Canvas

        val stepX = width / (pointsData.size - 1).coerceAtLeast(1)

        // Helper to get coordinates
        fun getPoint(index: Int): Offset {
            val p = pointsData[index]
            val x = stepX * index
            // Invert Y (1 - p) so 1.0 is top, 0.0 is bottom of graph area
            // Add topPadding to shift graph down
            val y = topPadding + graphHeight * (1 - p)
            return Offset(x, y)
        }

        // Draw Graph Path
        val path = Path().apply {
            val first = getPoint(0)
            moveTo(first.x, first.y)
            
            for (i in 0 until pointsData.size - 1) {
                val p1 = getPoint(i)
                val p2 = getPoint(i + 1)
                
                // Cubic Bezier
                val cx = (p1.x + p2.x) / 2f
                cubicTo(cx, p1.y, cx, p2.y, p2.x, p2.y)
            }
        }

        // Draw Fill
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, topPadding + graphHeight)
            lineTo(0f, topPadding + graphHeight)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    graphColor.copy(alpha = 0.2f),
                    graphColor.copy(alpha = 0.0f)
                ),
                startY = topPadding,
                endY = topPadding + graphHeight
            )
        )

        // Draw Line
        drawPath(
            path = path,
            color = graphColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw Dot only for the most recent point
        if (histories.isNotEmpty()) {
            val lastIndex = pointsData.size - 1
            val lastPos = getPoint(lastIndex)
            drawCircle(
                color = AppColors.White,
                radius = 4.dp.toPx(),
                center = lastPos
            )
            drawCircle(
                color = graphColor,
                radius = 4.dp.toPx(),
                center = lastPos,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Draw X-Axis Labels (Exclude first and last dates to avoid clipping)
        if (histories.size > 2) {
            histories.indices.forEach { i ->
                if (i == 0 || i == histories.size - 1) return@forEach

                val dateStr = formatToMonthDay(histories[i].datetime)
                val xPos = getPoint(i).x

                val textLayoutResult = textMeasurer.measure(
                    text = AnnotatedString(dateStr),
                    style = TextStyle(
                        color = AppColors.TextGray2,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                )

                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        x = xPos - textLayoutResult.size.width / 2,
                        y = height - bottomPadding + 10.dp.toPx()
                    )
                )
            }
        }

        // Tooltip logic
        if (selectedIndex != null && selectedIndex in histories.indices) {
            val point = getPoint(selectedIndex)
            val history = histories[selectedIndex]
            
            // Draw Selected Dot (Larger)
            drawCircle(
                color = AppColors.White,
                radius = 7.dp.toPx(),
                center = point
            )
            drawCircle(
                color = AppColors.Black, // Active selection dot color
                radius = 7.dp.toPx(),
                center = point,
                style = Stroke(width = 3.dp.toPx())
            )

            // Prepare Tooltip Text
            val dateText = formatToMonthDay(history.datetime)
            val priceText = "${NumberFormat.getNumberInstance(Locale.US).format(history.price)}원"
            
            val dateLayout = textMeasurer.measure(
                text = AnnotatedString(dateText),
                style = TextStyle(color = AppColors.IconGray2, fontSize = 12.sp)
            )
            val priceLayout = textMeasurer.measure(
                text = AnnotatedString(priceText),
                style = TextStyle(color = AppColors.White, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            )
            
            val tooltipWidth = maxOf(dateLayout.size.width, priceLayout.size.width) + 24.dp.toPx()
            val tooltipHeight = dateLayout.size.height + priceLayout.size.height + 20.dp.toPx()
            
            // Tooltip Position (Above the point)
            // Clamp to screen bounds logic is tricky inside scrollable canvas because 'width' is huge.
            // We should just draw it relative to the point. The scroll view handles clipping.
            // However, we want to avoid it going off the specific canvas bounds (left/right of total graph width)
            
            var tooltipX = point.x - tooltipWidth / 2
            var tooltipY = point.y - tooltipHeight - 10.dp.toPx()
            
            if (tooltipX < 0) tooltipX = 0f
            if (tooltipX + tooltipWidth > width) tooltipX = width - tooltipWidth
            
            // Draw Tooltip Box
            drawRoundRect(
                color = tooltipColor,
                topLeft = Offset(tooltipX, tooltipY),
                size = Size(tooltipWidth, tooltipHeight),
                cornerRadius = CornerRadius(8.dp.toPx())
            )
            
            // Draw Text inside
            drawText(
                textLayoutResult = dateLayout,
                topLeft = Offset(
                    x = tooltipX + (tooltipWidth - dateLayout.size.width) / 2,
                    y = tooltipY + 8.dp.toPx()
                )
            )
            
            drawText(
                textLayoutResult = priceLayout,
                topLeft = Offset(
                    x = tooltipX + (tooltipWidth - priceLayout.size.width) / 2,
                    y = tooltipY + 8.dp.toPx() + dateLayout.size.height
                )
            )
        }
    }
}

// Helper extension for math round
private fun Float.roundToInt(): Int {
    return kotlin.math.round(this).toInt()
}

// Simple date formatter (assuming ISO or similar string, otherwise passthrough)
// Adjust parsing logic based on actual data format
fun formatToMonthDay(dateString: String): String {
    // Example input: "2023-12-19T10:00:00" or "2023-12-19"
    // We want "12/19"
    return try {
        // Very basic parsing to avoid heavy DateTime deps if not present, or use java.time
        // Assuming format yyyy-MM-dd...
        if (dateString.length >= 10) {
            val month = dateString.substring(5, 7)
            val day = dateString.substring(8, 10)
            "$month/$day"
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

@Preview
@Composable
fun HistoryGraphPreview() {
    HistoryGraph(histories = listOf(
        PriceHistory("2023-11-26", 10000),
        PriceHistory("2023-12-06", 12000),
        PriceHistory("2023-12-16", 11000),
        PriceHistory("2023-12-26", 14000),
        PriceHistory("2024-01-05", 13000),
        PriceHistory("2024-01-15", 15000),
        PriceHistory("2024-01-20", 15500),
        PriceHistory("2024-01-25", 16000),
    ))
}

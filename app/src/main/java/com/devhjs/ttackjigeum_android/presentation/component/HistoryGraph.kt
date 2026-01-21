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
            
            // 날짜별로 정렬하여 올바른 시간 순서 보장
            val sortedHistories = remember(histories) {
                histories.sortedBy { it.datetime }
            }
            
            // 인터랙티브 툴팁을 위한 상태
            var selectedIndex by remember { mutableStateOf<Int?>(null) }
            val textMeasurer = rememberTextMeasurer()
            val scrollState = rememberScrollState()

            // 데이터 로드 시 가장 최근 날짜(끝)로 자동 스크롤
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
                        // 빈 상태 모의 그래프 (정적)
                         GraphCanvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .alpha(0.3f),
                            histories = emptyList(), // 내부에서 모의 데이터를 트리거함
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
                    // 아이템 개수를 기반으로 필요한 너비 계산
                    // 최소 화면 너비 보장, 그렇지 않으면 확장
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
    val tooltipColor = Color(0xFF424242) // 툴팁 배경색 (다크 그레이)
    
    // 포인트 데이터 안전 파싱
    val pointsData = remember(histories) {
        if (histories.isEmpty()) {
            // 빈 상태 배경을 위한 모의 데이터
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
                        
                        // 가장 가까운 인덱스 찾기
                        val index = (offset.x / stepX).roundToInt().coerceIn(0, histories.size - 1)
                        onPointSelected(index)
                    }
                }
            )
        }
    ) {
        val width = size.width
        val height = size.height
        
        // 그리기 영역 정의 (하단 라벨 및 상단 툴팁 여백)
        val bottomPadding = 40.dp.toPx()
        val topPadding = 60.dp.toPx()
        val graphHeight = height - bottomPadding - topPadding
        
        if (pointsData.isEmpty()) return@Canvas

        val stepX = width / (pointsData.size - 1).coerceAtLeast(1)

        // 좌표 계산 헬퍼 로직
        fun getPoint(index: Int): Offset {
            val p = pointsData[index]
            val x = stepX * index
            // Y축 반전 (1 - p): 1.0이 상단, 0.0이 그래프 영역 하단
            // 상단 여백 추가하여 그래프 아래로 이동
            val y = topPadding + graphHeight * (1 - p)
            return Offset(x, y)
        }

        // 그래프 경로 그리기
        val path = Path().apply {
            val first = getPoint(0)
            moveTo(first.x, first.y)
            
            for (i in 0 until pointsData.size - 1) {
                val p1 = getPoint(i)
                val p2 = getPoint(i + 1)
                
                // 3차 베지에 곡선 적용
                val cx = (p1.x + p2.x) / 2f
                cubicTo(cx, p1.y, cx, p2.y, p2.x, p2.y)
            }
        }

        // 채우기 영역 그리기
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

        // 선 그리기
        drawPath(
            path = path,
            color = graphColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // 가장 최근 포인트에만 점 그리기 로직
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

        // X축 라벨 그리기 (클리핑 방지를 위해 첫 번째와 마지막 날짜 제외)
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

        // 툴팁 관련 로직
        if (selectedIndex != null && selectedIndex in histories.indices) {
            val point = getPoint(selectedIndex)
            val history = histories[selectedIndex]
            
            // 선택된 점 그리기 (강조)
            drawCircle(
                color = AppColors.White,
                radius = 7.dp.toPx(),
                center = point
            )
            drawCircle(
                color = AppColors.Black, // 활성화된 선택 점 색상
                radius = 7.dp.toPx(),
                center = point,
                style = Stroke(width = 3.dp.toPx())
            )

            // 툴팁 텍스트 준비 로직
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
            
            // 툴팁 위치 선정 (포인트 위)
            // 'width'가 매우 크기 때문에 스크롤 가능한 캔버스 내에서 화면 클램핑 로직은 다소 복잡함.
            // 포인트 좌표를 기준으로 그리며 스크롤 뷰가 클리핑을 담당함.
            // 단, 전체 그래프 너비의 좌우 경계를 넘지 않도록 조정.
            
            var tooltipX = point.x - tooltipWidth / 2
            var tooltipY = point.y - tooltipHeight - 10.dp.toPx()
            
            if (tooltipX < 0) tooltipX = 0f
            if (tooltipX + tooltipWidth > width) tooltipX = width - tooltipWidth
            
            // 툴팁 상자 그리기
            drawRoundRect(
                color = tooltipColor,
                topLeft = Offset(tooltipX, tooltipY),
                size = Size(tooltipWidth, tooltipHeight),
                cornerRadius = CornerRadius(8.dp.toPx())
            )
            
            // 상자 내부 텍스트 그리기
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

// 수학적 반올림을 위한 헬퍼 확장 함수
private fun Float.roundToInt(): Int {
    return kotlin.math.round(this).toInt()
}

// 간단한 날짜 포맷터 (ISO 등 표준 포맷 가정)
// 데이터의 실제 형식에 맞게 파싱 로직을 조정하십시오.
fun formatToMonthDay(dateString: String): String {
    // 예: "2023-12-19T10:00:00" -> "12/19"
    return try {
        // 중복 의존성을 피하기 위한 매우 기초적인 파싱 (yyyy-MM-dd 형태 가정)
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

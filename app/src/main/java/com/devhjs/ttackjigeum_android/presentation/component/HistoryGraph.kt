package com.devhjs.ttackjigeum_android.presentation.component


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.ui.theme.AppColors
import com.devhjs.ttackjigeum_android.ui.theme.AppTextStyles

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

            // Mock Data and Graph
            // Using a simple Canvas implementation for visual representation
            // Normalizing price data for graph
            val isEmpty = histories.isEmpty()
            val points = if (!isEmpty) {
                val maxPrice = histories.maxOfOrNull { it.price } ?: 1
                val minPrice = histories.minOfOrNull { it.price } ?: 0
                val range = (maxPrice - minPrice).takeIf { it > 0 } ?: 1
                
                histories.map { (it.price - minPrice).toFloat() / range }
            } else {
                listOf(0.5f, 0.6f, 0.4f, 0.7f, 0.5f) // Default mock if empty
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                GraphCanvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .then(if (isEmpty) Modifier.alpha(0.3f) else Modifier),
                    points = points
                )

                if (isEmpty) {
                    Text(
                        text = "해당 상품은 딱지금에 완전 처음 추가된 상품이라\n가격 그래프가 존재하지 않아요!\n내일부터 가격 그래프를 그려드릴게요!",
                        style = AppTextStyles.smallTextRegular,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = AppColors.Black
                    )
                }
            }
            
            // Labels (Simplified)
            // Ideally should be drawn on canvas or using a Row below
        }
    }
}

@Composable
fun GraphCanvas(
    modifier: Modifier = Modifier,
    points: List<Float>
) {
    val graphColor = AppColors.Primary // Green
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Points are passed as parameter
        if (points.isEmpty()) return@Canvas
        
        val stepX = width / (points.size - 1)
        
        val path = Path().apply {
            moveTo(0f, height * (1 - points[0]))
            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i+1]
                
                val x1 = stepX * i
                val x2 = stepX * (i + 1)
                
                val y1 = height * (1 - p1)
                val y2 = height * (1 - p2)
                
                // Quadratic bezier for smoothing
                val cx = (x1 + x2) / 2f
                
                cubicTo(cx, y1, cx, y2, x2, y2)
            }
        }
        
        // Draw Fill Gradient
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    graphColor.copy(alpha = 0.2f),
                    graphColor.copy(alpha = 0.0f)
                )
            )
        )
        
        // Draw Line
        drawPath(
            path = path,
            color = graphColor,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Draw End Circle
        val lastIndex = points.size - 1
        val lastX = stepX * lastIndex
        val lastY = height * (1 - points[lastIndex])
        
        drawCircle(
            color = AppColors.White,
            radius = 6.dp.toPx(),
            center = Offset(lastX, lastY)
        )
        drawCircle(
            color = graphColor,
            radius = 6.dp.toPx(),
            center = Offset(lastX, lastY),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Preview
@Composable
fun HistoryGraphPreview() {
    HistoryGraph(histories = emptyList())
}

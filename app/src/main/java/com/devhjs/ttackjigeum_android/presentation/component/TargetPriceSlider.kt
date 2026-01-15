package com.devhjs.ttackjigeum_android.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devhjs.ttackjigeum_android.ui.theme.AppTextStyles
import com.devhjs.ttackjigeum_android.ui.theme.AppColors

@Composable
fun TargetPriceSlider(
    modifier: Modifier = Modifier,
    targetPrice: Int,
    currentPrice: Int,
    onTargetPriceChange: (Int) -> Unit
) {
    // Determine min/max range based on prices
    // Example logic: Min = 0, Max = Current Price * 1.5 (or similar)
    // For now, let's keep hardcoded range logic or passed params.
    // User asked to modify data.
    // Let's assume range is dynamic.
    val minPrice = (currentPrice * 0.5).toInt() // Example: 50% of current
    val maxPrice = (currentPrice * 1.2).toInt() // Example: 120% of current
    var sliderValue by remember(targetPrice) { mutableFloatStateOf(targetPrice.toFloat()) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "목표가 설정",
                        style = AppTextStyles.mediumTextBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "가격 하락 시 알림을 보내드립니다.",
                        style = AppTextStyles.smallTextRegular.copy(
                            color = AppColors.TextGray2
                        )
                    )
                }
                
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.IconGray2),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "₩${String.format("%,d", sliderValue.toInt())}",
                        style = AppTextStyles.mediumTextBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            @OptIn(ExperimentalMaterial3Api::class)
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = {
                    onTargetPriceChange(sliderValue.toInt())
                },
                valueRange = minPrice.toFloat()..maxPrice.toFloat(),
                thumb = {
                    // Custom Thumb
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .shadow(4.dp, shape = CircleShape)
                            .background(AppColors.White, CircleShape)
                            .padding(8.dp)
                            .background(AppColors.Primary, CircleShape)
                    )
                },
                track = { sliderState ->
                    // Custom Track
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier.height(10.dp),
                        colors = SliderDefaults.colors(
                            activeTrackColor = AppColors.Primary,
                            inactiveTrackColor = AppColors.IconGray2
                        ),
                        thumbTrackGapSize = 0.dp
                    )
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "₩${String.format("%,d", minPrice)}",
                    style = AppTextStyles.smallerTextBold.copy(
                        color = AppColors.TextGray2
                    )
                )
                Text(
                    text = "₩${String.format("%,d", maxPrice)}",
                    style = AppTextStyles.smallerTextBold.copy(
                        color = AppColors.TextGray2
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun TargetPriceSliderPreview() {
    TargetPriceSlider(
        targetPrice = 320000,
        currentPrice = 348000,
        onTargetPriceChange = {}
    )
}

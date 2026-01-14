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

@Composable
fun TargetPriceSlider(
    modifier: Modifier = Modifier,
    currentPrice: Int = 348000,
    minPrice: Int = 250000,
    maxPrice: Int = 400000
) {
    var sliderValue by remember { mutableFloatStateOf(320000f) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                            color = Color.Gray
                        )
                    )
                }
                
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "₩${sliderValue.toInt()}",
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
                valueRange = minPrice.toFloat()..maxPrice.toFloat(),
                thumb = {
                    // Custom Thumb
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .shadow(4.dp, shape = CircleShape)
                            .background(Color.White, CircleShape)
                            .padding(8.dp)
                            .background(Color(0xFF00C853), CircleShape)
                    )
                },
                track = { sliderState ->
                    // Custom Track
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier.height(10.dp),
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color(0xFF00C853),
                            inactiveTrackColor = Color(0xFFEEEEEE)
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
                    text = "₩$minPrice",
                    style = AppTextStyles.smallerTextBold.copy(
                        color = Color.Gray
                    )
                )
                Text(
                    text = "₩$maxPrice",
                    style = AppTextStyles.smallerTextBold.copy(
                        color = Color.Gray
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun TargetPriceSliderPreview() {
    TargetPriceSlider()
}

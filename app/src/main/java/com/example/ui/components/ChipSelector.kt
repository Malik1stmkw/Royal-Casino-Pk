package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldGradientEnd
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.util.SoundEffectsManager

@Composable
fun ChipBetSelector(
    selectedAmount: Double,
    onAmountSelected: (Double) -> Unit
) {
    val chipValues = listOf(100.0, 500.0, 1000.0, 5000.0, 10000.0)
    val colors = listOf(
        listOf(Color(0xFF3F51B5), Color(0xFF2196F3)),
        listOf(NeonGreen, Color(0xFF00B0FF)),
        listOf(NeonRed, Color(0xFFFF5252)),
        listOf(GoldPrimary, GoldGradientEnd),
        listOf(Color(0xFFAA00FF), Color(0xFFE040FB))
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        chipValues.forEachIndexed { index, amount ->
            val isSelected = selectedAmount == amount
            val chipColors = colors[index % colors.size]

            Box(
                modifier = Modifier
                    .size(if (isSelected) 54.dp else 46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(chipColors)
                    )
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) GoldPrimary else Color.White.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .clickable {
                        SoundEffectsManager.playChipSound()
                        onAmountSelected(amount)
                    }
                    .testTag("chip_${amount.toInt()}"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 42.dp else 36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (amount >= 1000) "${(amount / 1000).toInt()}k" else "${amount.toInt()}",
                        color = Color.White,
                        fontSize = if (isSelected) 13.sp else 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

package com.example.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ChipBetSelector
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.CasinoDarkBackground
import com.example.ui.theme.CasinoSurfaceDark
import com.example.ui.theme.GoldGradientEnd
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CasinoViewModel
import kotlinx.coroutines.launch

@Composable
fun WheelOfFortuneGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    val prizes = listOf(1.5, 2.0, 3.0, 5.0, 10.0, 20.0, 50.0, 100.0)
    var betAmount by remember { mutableStateOf(500.0) }
    var rotationAngle by remember { mutableStateOf(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var winMessage by remember { mutableStateOf("Spin the Fortune Wheel!") }

    val rotationAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun spinWheel() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            winMessage = "Insufficient balance!"
            return
        }

        isSpinning = true
        winMessage = "Spinning Wheel..."

        val prizeIndex = (0..7).random()
        val prizeMult = prizes[prizeIndex]
        val targetRotation = 360f * 5 + (prizeIndex * (360f / 8f))

        scope.launch {
            rotationAnim.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(durationMillis = 3500)
            )

            val payout = betAmount * prizeMult
            viewModel.recordGamePlay(10, "Lucky Wheel Spin", betAmount, payout)
            winMessage = "🎉 WINNER! Hit ${prizeMult}x! Won Rs. ${String.format("%.0f", payout)}"
            isSpinning = false
        }
    }

    Scaffold(
        containerColor = CasinoDarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CasinoSurfaceDark)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("wheel_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("🎯 Lucky Wheel Spin", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Multipliers Up To 100x Jackpot", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Wheel Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CasinoCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Arrow pointer
                    Text("🔻", fontSize = 28.sp)

                    // Wheel Graphic
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .rotate(rotationAnim.value)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(GoldPrimary, NeonCyan, NeonPurple, NeonGreen, NeonRed, GoldGradientEnd)
                                )
                            )
                            .border(4.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎯 100x", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = winMessage,
                        color = if (winMessage.contains("WINNER")) NeonGreen else GoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Controls
            Column(modifier = Modifier.fillMaxWidth()) {
                ChipBetSelector(selectedAmount = betAmount, onAmountSelected = { betAmount = it })
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { spinWheel() },
                    enabled = !isSpinning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("spin_wheel_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text(if (isSpinning) "SPINNING..." else "SPIN WHEEL (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

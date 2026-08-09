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
import com.example.ui.theme.CasinoSurfaceVariant
import com.example.ui.theme.GoldGradientEnd
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CasinoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SlotsGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    val symbols = listOf("7️⃣", "💎", "🔔", "🍇", "🍋", "🍒")
    var betAmount by remember { mutableStateOf(500.0) }
    var reel1 by remember { mutableStateOf("7️⃣") }
    var reel2 by remember { mutableStateOf("7️⃣") }
    var reel3 by remember { mutableStateOf("7️⃣") }
    var isSpinning by remember { mutableStateOf(false) }
    var winResultText by remember { mutableStateOf("Press SPIN to play!") }
    var lastWinAmount by remember { mutableStateOf(0.0) }

    val scope = rememberCoroutineScope()

    fun spinReels() {
        if (isSpinning) return
        val currentBalance = viewModel.userProfile.value?.balance ?: 0.0
        if (currentBalance < betAmount) {
            winResultText = "Insufficient balance!"
            return
        }

        isSpinning = true
        winResultText = "Spinning..."
        lastWinAmount = 0.0

        scope.launch {
            // Rapid shuffling animation
            for (i in 0..15) {
                reel1 = symbols.random()
                reel2 = symbols.random()
                reel3 = symbols.random()
                com.example.util.SoundEffectsManager.playSpinSound()
                delay(80)
            }

            // Final Result
            val r1 = symbols.random()
            val r2 = symbols.random()
            val r3 = symbols.random()
            reel1 = r1
            reel2 = r2
            reel3 = r3

            var multiplier = 0.0
            if (r1 == r2 && r2 == r3) {
                multiplier = when (r1) {
                    "7️⃣" -> 50.0
                    "💎" -> 25.0
                    "🔔" -> 10.0
                    "🍇" -> 5.0
                    "🍋" -> 3.0
                    else -> 2.0
                }
            } else if (r1 == r2 || r2 == r3 || r1 == r3) {
                multiplier = 1.5
            }

            val payout = betAmount * multiplier
            lastWinAmount = payout
            viewModel.recordGamePlay(1, "Slots Jackpot", betAmount, payout)

            if (multiplier > 0) {
                winResultText = "🎉 MEGA WIN! Rs. ${String.format("%.0f", payout)} (${multiplier}x)"
            } else {
                winResultText = "Try Again!"
            }
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("slots_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("🎰 Slots Jackpot", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("3-Reel Classic Fruit Machine", color = TextSecondary, fontSize = 11.sp)
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
            // Slot Machine Display Cabinet
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        brush = Brush.verticalGradient(listOf(GoldPrimary, CasinoCardBorder)),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "JACKPOT MULTIPLIER 50x",
                        color = GoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Reels Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(16.dp))
                            .border(1.dp, CasinoCardBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(reel1, reel2, reel3).forEach { symbol ->
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(CasinoSurfaceVariant, Color(0xFF100A26))
                                        )
                                    )
                                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(symbol, fontSize = 42.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = winResultText,
                        color = if (lastWinAmount > 0) NeonGreen else if (winResultText.contains("Insufficient")) NeonRed else TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bet Selector & Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SELECT BET AMOUNT (RS)",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                ChipBetSelector(
                    selectedAmount = betAmount,
                    onAmountSelected = { betAmount = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { spinReels() },
                    enabled = !isSpinning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("spin_slots_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = if (isSpinning) "SPINNING..." else "SPIN (RS. ${betAmount.toInt()})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

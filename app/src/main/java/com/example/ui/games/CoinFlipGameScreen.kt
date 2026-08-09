package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CasinoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CoinFlipGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    var choice by remember { mutableStateOf("HEADS") } // HEADS, TAILS
    var betAmount by remember { mutableStateOf(500.0) }
    var coinResult by remember { mutableStateOf("👑") }
    var isFlipping by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("Choose Heads or Tails & Flip!") }

    val scope = rememberCoroutineScope()

    fun flipCoin() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            resultText = "Insufficient balance!"
            return
        }

        isFlipping = true
        resultText = "Flipping Coin..."

        scope.launch {
            for (i in 0..12) {
                coinResult = if (i % 2 == 0) "👑" else "🦅"
                delay(100)
            }

            val isHeads = Math.random() < 0.5
            val finalSide = if (isHeads) "HEADS" else "TAILS"
            coinResult = if (isHeads) "👑" else "🦅"

            val win = choice == finalSide
            val payout = if (win) betAmount * 2.0 else 0.0

            viewModel.recordGamePlay(5, "Coin Flip 3D", betAmount, payout)

            if (win) {
                resultText = "🎉 WON Rs. ${String.format("%.0f", payout)}! It landed on $finalSide!"
            } else {
                resultText = "Landed on $finalSide. Try Again!"
            }

            isFlipping = false
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("coinflip_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("🪙 Coin Flip 3D", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("2x Instant Double or Nothing", color = TextSecondary, fontSize = 11.sp)
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
            // Coin Display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CasinoCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(GoldPrimary, GoldGradientEnd)
                                )
                            )
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(coinResult, fontSize = 54.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = resultText,
                        color = if (resultText.contains("WON")) NeonGreen else if (resultText.contains("Landed")) NeonRed else TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Choice Buttons
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("SELECT YOUR SIDE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("HEADS" to "👑 HEADS (2x)", "TAILS" to "🦅 TAILS (2x)").forEach { (side, label) ->
                        val isSel = choice == side
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CasinoSurfaceDark)
                                .border(
                                    width = if (isSel) 2.dp else 1.dp,
                                    color = if (isSel) GoldPrimary else CasinoCardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { choice = side }
                                .testTag("coin_$side"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, color = if (isSel) GoldPrimary else Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                ChipBetSelector(selectedAmount = betAmount, onAmountSelected = { betAmount = it })

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { flipCoin() },
                    enabled = !isFlipping,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("flip_coin_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text(if (isFlipping) "FLIPPING..." else "FLIP COIN (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

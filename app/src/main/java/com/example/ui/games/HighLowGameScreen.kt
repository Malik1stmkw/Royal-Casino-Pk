package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ChipBetSelector
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.CasinoDarkBackground
import com.example.ui.theme.CasinoSurfaceDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CasinoViewModel

@Composable
fun HighLowGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    val ranks = listOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")
    val suits = listOf("♠️", "♥️", "♦️", "♣️")

    fun drawCard(): PlayingCard {
        val r = ranks.random()
        val valNum = ranks.indexOf(r) + 2
        return PlayingCard(r, suits.random(), valNum)
    }

    var betAmount by remember { mutableStateOf(500.0) }
    var currentCard by remember { mutableStateOf(drawCard()) }
    var streak by remember { mutableStateOf(0) }
    var isStreakActive by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("Guess if next card is HIGHER or LOWER!") }

    fun guess(isHigher: Boolean) {
        val next = drawCard()
        val correct = if (isHigher) next.value >= currentCard.value else next.value <= currentCard.value
        currentCard = next

        if (correct) {
            streak++
            isStreakActive = true
            val multiplier = 1.0 + (streak * 0.5)
            val payout = betAmount * multiplier
            viewModel.recordGamePlay(14, "High-Low Card", betAmount, payout)
            resultText = "🎉 Correct! Streak $streak (${String.format("%.1f", multiplier)}x Win Rs. ${String.format("%.0f", payout)})"
        } else {
            streak = 0
            isStreakActive = false
            viewModel.recordGamePlay(14, "High-Low Card", betAmount, 0.0)
            resultText = "💥 Wrong guess! Streak reset."
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("highlow_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("📈 High-Low Card Guess", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Build Winning Card Streaks", color = TextSecondary, fontSize = 11.sp)
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
            // Card Arena
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
                    Text("CURRENT CARD", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    CardView(currentCard)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = resultText,
                        color = if (resultText.contains("Correct")) NeonGreen else if (resultText.contains("Wrong")) NeonRed else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // High or Low Buttons
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { guess(true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("highlow_higher"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black)
                    ) {
                        Text("HIGHER 📈", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Button(
                        onClick = { guess(false) },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("highlow_lower"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White)
                    ) {
                        Text("LOWER 📉", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                ChipBetSelector(selectedAmount = betAmount, onAmountSelected = { betAmount = it })
            }
        }
    }
}

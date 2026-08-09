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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CasinoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BaccaratGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    val suits = listOf("♠️", "♥️", "♦️", "♣️")

    fun drawCard(): PlayingCard {
        val r = ranks.random()
        val valNum = when (r) {
            "A" -> 1
            "10", "J", "Q", "K" -> 0
            else -> r.toInt()
        }
        return PlayingCard(r, suits.random(), valNum)
    }

    var betOn by remember { mutableStateOf("PLAYER") } // PLAYER, BANKER, TIE
    var betAmount by remember { mutableStateOf(500.0) }
    val playerCards = remember { mutableStateListOf<PlayingCard>() }
    val bankerCards = remember { mutableStateListOf<PlayingCard>() }
    var isDealing by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("Place bet on Player, Banker or Tie!") }

    val scope = rememberCoroutineScope()

    fun playBaccarat() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            resultText = "Insufficient balance!"
            return
        }

        isDealing = true
        playerCards.clear()
        bankerCards.clear()
        resultText = "Dealing Cards..."

        scope.launch {
            delay(300)
            playerCards.add(drawCard())
            bankerCards.add(drawCard())
            delay(300)
            playerCards.add(drawCard())
            bankerCards.add(drawCard())
            delay(300)

            val pTotal = (playerCards.sumOf { it.value }) % 10
            val bTotal = (bankerCards.sumOf { it.value }) % 10

            var multiplier = 0.0
            if (pTotal > bTotal) {
                if (betOn == "PLAYER") multiplier = 2.0
            } else if (bTotal > pTotal) {
                if (betOn == "BANKER") multiplier = 1.95
            } else {
                if (betOn == "TIE") multiplier = 9.0 else multiplier = 1.0 // Push
            }

            val payout = betAmount * multiplier
            viewModel.recordGamePlay(4, "Baccarat Royale", betAmount, payout)

            if (pTotal == bTotal) {
                resultText = "🤝 TIE ($pTotal vs $bTotal)! ${if (betOn == "TIE") "WON Rs. ${String.format("%.0f", payout)}" else "Push"}"
            } else if ((pTotal > bTotal && betOn == "PLAYER") || (bTotal > pTotal && betOn == "BANKER")) {
                resultText = "🎉 WON Rs. ${String.format("%.0f", payout)}! ($pTotal vs $bTotal)"
            } else {
                resultText = "Lost! ($pTotal vs $bTotal)"
            }

            isDealing = false
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("baccarat_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("🃏 Baccarat Royale", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Closest Hand to 9 Wins", color = TextSecondary, fontSize = 11.sp)
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
            // Hands Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val pSum = if (playerCards.isEmpty()) 0 else (playerCards.sumOf { it.value }) % 10
                        Text("PLAYER ($pSum)", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            playerCards.forEach { CardView(it) }
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val bSum = if (bankerCards.isEmpty()) 0 else (bankerCards.sumOf { it.value }) % 10
                        Text("BANKER ($bSum)", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            bankerCards.forEach { CardView(it) }
                        }
                    }
                }
            }

            Text(
                text = resultText,
                color = if (resultText.contains("WON")) NeonGreen else TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            // Bet Selection
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("PLACE YOUR BET", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("PLAYER" to "Player (2x)", "TIE" to "Tie (9x)", "BANKER" to "Banker (1.95x)").forEach { (type, label) ->
                        val isSel = betOn == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CasinoSurfaceDark)
                                .border(
                                    width = if (isSel) 2.dp else 1.dp,
                                    color = if (isSel) GoldPrimary else CasinoCardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { betOn = type }
                                .testTag("baccarat_bet_$type"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, color = if (isSel) GoldPrimary else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                ChipBetSelector(selectedAmount = betAmount, onAmountSelected = { betAmount = it })

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { playBaccarat() },
                    enabled = !isDealing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("baccarat_deal"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text(if (isDealing) "DEALING..." else "DEAL BACCARAT (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

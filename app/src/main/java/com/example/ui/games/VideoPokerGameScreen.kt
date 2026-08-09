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
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CasinoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoPokerGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    val ranks = listOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")
    val suits = listOf("♠️", "♥️", "♦️", "♣️")

    fun drawCard(): PlayingCard {
        val r = ranks.random()
        val v = ranks.indexOf(r) + 2
        return PlayingCard(r, suits.random(), v)
    }

    var betAmount by remember { mutableStateOf(500.0) }
    val hand = remember { mutableStateListOf<PlayingCard>() }
    val isHeld = remember { mutableStateListOf(false, false, false, false, false) }
    var gameStage by remember { mutableStateOf("IDLE") } // IDLE, DEAL, END
    var resultText by remember { mutableStateOf("Press Deal for 5-Card Video Poker!") }

    val scope = rememberCoroutineScope()

    fun dealCards() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            resultText = "Insufficient balance!"
            return
        }

        hand.clear()
        repeat(5) {
            hand.add(drawCard())
            isHeld[it] = false
        }

        gameStage = "DEAL"
        resultText = "Tap cards to HOLD, then DRAW!"
    }

    fun drawReplacement() {
        if (gameStage != "DEAL") return

        for (i in 0..4) {
            if (!isHeld[i]) {
                hand[i] = drawCard()
            }
        }

        // Evaluate Hand
        val counts = hand.groupingBy { it.rank }.eachCount()
        val suitsCount = hand.groupingBy { it.suit }.eachCount()

        val isFlush = suitsCount.values.any { it == 5 }
        val values = hand.map { it.value }.sorted()
        val isStraight = values[4] - values[0] == 4 && values.distinct().size == 5

        var multiplier = 0.0
        var handName = "High Card"

        if (isFlush && isStraight && values[0] == 10) {
            multiplier = 250.0
            handName = "🔥 ROYAL FLUSH (250x)"
        } else if (isFlush && isStraight) {
            multiplier = 50.0
            handName = "⚡ STRAIGHT FLUSH (50x)"
        } else if (counts.containsValue(4)) {
            multiplier = 25.0
            handName = "🎰 FOUR OF A KIND (25x)"
        } else if (counts.containsValue(3) && counts.containsValue(2)) {
            multiplier = 9.0
            handName = "🏠 FULL HOUSE (9x)"
        } else if (isFlush) {
            multiplier = 6.0
            handName = "🎨 FLUSH (6x)"
        } else if (isStraight) {
            multiplier = 4.0
            handName = "📏 STRAIGHT (4x)"
        } else if (counts.containsValue(3)) {
            multiplier = 3.0
            handName = "☘️ THREE OF A KIND (3x)"
        } else if (counts.filter { it.value == 2 }.size == 2) {
            multiplier = 2.0
            handName = "👯 TWO PAIR (2x)"
        } else if (counts.any { (rank, count) -> count == 2 && listOf("J", "Q", "K", "A").contains(rank) }) {
            multiplier = 1.0
            handName = "🃏 JACKS OR BETTER (1x)"
        }

        val payout = betAmount * multiplier
        viewModel.recordGamePlay(16, "Video Poker", betAmount, payout)

        if (multiplier > 0) {
            resultText = "🎉 WON Rs. ${String.format("%.0f", payout)} ($handName)!"
        } else {
            resultText = "No Pair or Better. Try Again!"
        }

        gameStage = "END"
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("vp_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("📹 Video Poker", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Jacks or Better 5-Card Draw", color = TextSecondary, fontSize = 11.sp)
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
            // Hand Display
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
                    Text("5 CARD HAND", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(5) { idx ->
                            val card = hand.getOrNull(idx)
                            val held = isHeld.getOrElse(idx) { false }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .clickable { if (gameStage == "DEAL") isHeld[idx] = !held }
                                        .testTag("vp_card_$idx")
                                ) {
                                    if (card != null) CardView(card) else Box(modifier = Modifier.size(54.dp, 76.dp).background(Color.DarkGray, RoundedCornerShape(8.dp)))
                                }
                                if (held) {
                                    Text("HELD", color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = resultText,
                        color = if (resultText.contains("WON")) NeonGreen else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Controls
            Column(modifier = Modifier.fillMaxWidth()) {
                if (gameStage == "DEAL") {
                    Button(
                        onClick = { drawReplacement() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("vp_draw_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("DRAW CARDS 🃏", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    ChipBetSelector(selectedAmount = betAmount, onAmountSelected = { betAmount = it })
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { dealCards() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("vp_deal_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("DEAL HAND (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

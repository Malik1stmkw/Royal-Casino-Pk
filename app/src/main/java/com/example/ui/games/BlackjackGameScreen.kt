package com.example.ui.games

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
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CasinoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PlayingCard(val rank: String, val suit: String, val value: Int)

@Composable
fun BlackjackGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    val suits = listOf("♠️", "♥️", "♦️", "♣️")
    val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

    fun drawRandomCard(): PlayingCard {
        val rank = ranks.random()
        val suit = suits.random()
        val value = when (rank) {
            "A" -> 11
            "J", "Q", "K" -> 10
            else -> rank.toInt()
        }
        return PlayingCard(rank, suit, value)
    }

    fun calcHandScore(cards: List<PlayingCard>): Int {
        var sum = cards.sumOf { it.value }
        var aceCount = cards.count { it.rank == "A" }
        while (sum > 21 && aceCount > 0) {
            sum -= 10
            aceCount--
        }
        return sum
    }

    var betAmount by remember { mutableStateOf(500.0) }
    val playerCards = remember { mutableStateListOf<PlayingCard>() }
    val dealerCards = remember { mutableStateListOf<PlayingCard>() }
    var gameStatus by remember { mutableStateOf("IDLE") } // IDLE, PLAYING, ENDED
    var resultText by remember { mutableStateOf("Place bet and Deal!") }

    val scope = rememberCoroutineScope()

    fun dealGame() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            resultText = "Insufficient balance!"
            return
        }

        playerCards.clear()
        dealerCards.clear()

        playerCards.add(drawRandomCard())
        playerCards.add(drawRandomCard())

        dealerCards.add(drawRandomCard())

        gameStatus = "PLAYING"
        resultText = "Hit or Stand?"

        if (calcHandScore(playerCards) == 21) {
            // Natural Blackjack!
            gameStatus = "ENDED"
            val payout = betAmount * 2.5
            viewModel.recordGamePlay(3, "Blackjack 21", betAmount, payout)
            resultText = "🎉 BLACKJACK! WON Rs. ${String.format("%.0f", payout)}"
        }
    }

    fun hit() {
        if (gameStatus != "PLAYING") return
        playerCards.add(drawRandomCard())
        val score = calcHandScore(playerCards)
        if (score > 21) {
            gameStatus = "ENDED"
            viewModel.recordGamePlay(3, "Blackjack 21", betAmount, 0.0)
            resultText = "💥 BUST! Score $score"
        }
    }

    fun stand() {
        if (gameStatus != "PLAYING") return
        scope.launch {
            while (calcHandScore(dealerCards) < 17) {
                dealerCards.add(drawRandomCard())
                delay(400)
            }

            val pScore = calcHandScore(playerCards)
            val dScore = calcHandScore(dealerCards)

            gameStatus = "ENDED"
            if (dScore > 21 || pScore > dScore) {
                val payout = betAmount * 2.0
                viewModel.recordGamePlay(3, "Blackjack 21", betAmount, payout)
                resultText = "🎉 WON Rs. ${String.format("%.0f", payout)}! ($pScore vs $dScore)"
            } else if (pScore == dScore) {
                viewModel.recordGamePlay(3, "Blackjack 21", betAmount, betAmount)
                resultText = "🤝 PUSH! Bet returned."
            } else {
                viewModel.recordGamePlay(3, "Blackjack 21", betAmount, 0.0)
                resultText = "Dealer Wins ($dScore vs $pScore)"
            }
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("blackjack_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("♠️ Blackjack 21", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Beat Dealer to 21", color = TextSecondary, fontSize = 11.sp)
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
            // Dealer Hand Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DEALER HAND (${if (dealerCards.isEmpty()) 0 else calcHandScore(dealerCards)})",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        dealerCards.forEach { card ->
                            CardView(card)
                        }
                    }
                }
            }

            // Status Message
            Text(
                text = resultText,
                color = if (resultText.contains("WON") || resultText.contains("BLACKJACK")) NeonGreen else if (resultText.contains("BUST")) NeonRed else GoldPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Player Hand Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "YOUR HAND (${calcHandScore(playerCards)})",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        playerCards.forEach { card ->
                            CardView(card)
                        }
                    }
                }
            }

            // Controls
            Column(modifier = Modifier.fillMaxWidth()) {
                if (gameStatus == "PLAYING") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { hit() },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("blackjack_hit"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text("HIT 🃏", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Button(
                            onClick = { stand() },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("blackjack_stand"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black)
                        ) {
                            Text("STAND 🛑", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                } else {
                    ChipBetSelector(selectedAmount = betAmount, onAmountSelected = { betAmount = it })
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { dealGame() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("blackjack_deal"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("DEAL CARDS (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CardView(card: PlayingCard) {
    val isRed = card.suit == "♥️" || card.suit == "♦️"
    Box(
        modifier = Modifier
            .size(54.dp, 76.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(card.rank, color = if (isRed) Color.Red else Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Text(card.suit, fontSize = 16.sp)
        }
    }
}

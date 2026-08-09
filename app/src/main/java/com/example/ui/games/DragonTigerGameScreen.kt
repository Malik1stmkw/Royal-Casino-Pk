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

@Composable
fun DragonTigerGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    val suits = listOf("♠️", "♥️", "♦️", "♣️")

    fun drawCard(): PlayingCard {
        val r = ranks.random()
        val v = when (r) {
            "A" -> 1
            "J" -> 11
            "Q" -> 12
            "K" -> 13
            else -> r.toInt()
        }
        return PlayingCard(r, suits.random(), v)
    }

    var betSide by remember { mutableStateOf("DRAGON") } // DRAGON, TIGER, TIE
    var betAmount by remember { mutableStateOf(500.0) }
    var dragonCard by remember { mutableStateOf<PlayingCard?>(null) }
    var tigerCard by remember { mutableStateOf<PlayingCard?>(null) }
    var isDealing by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("Bet Dragon, Tiger or Tie & Fight!") }

    val scope = rememberCoroutineScope()

    fun fightDuel() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            resultText = "Insufficient balance!"
            return
        }

        isDealing = true
        dragonCard = null
        tigerCard = null
        resultText = "Flipping cards..."

        scope.launch {
            delay(300)
            val dCard = drawCard()
            dragonCard = dCard
            delay(400)
            val tCard = drawCard()
            tigerCard = tCard

            var multiplier = 0.0
            if (dCard.value > tCard.value) {
                if (betSide == "DRAGON") multiplier = 2.0
            } else if (tCard.value > dCard.value) {
                if (betSide == "TIGER") multiplier = 2.0
            } else {
                if (betSide == "TIE") multiplier = 11.0 else multiplier = 0.5 // Push half
            }

            val payout = betAmount * multiplier
            viewModel.recordGamePlay(8, "Dragon vs Tiger", betAmount, payout)

            if (dCard.value == tCard.value) {
                resultText = "🤝 TIE MATCH! ${if (betSide == "TIE") "WON Rs. ${String.format("%.0f", payout)}" else "Half bet returned."}"
            } else if (multiplier > 0) {
                val winner = if (dCard.value > tCard.value) "DRAGON" else "TIGER"
                resultText = "🎉 $winner WINS! WON Rs. ${String.format("%.0f", payout)}"
            } else {
                val winner = if (dCard.value > tCard.value) "DRAGON" else "TIGER"
                resultText = "Lost! $winner Won."
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("dt_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("🐉 Dragon vs Tiger 🐅", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("High Card Card Duel", color = TextSecondary, fontSize = 11.sp)
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
            // Battle Arena
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🐉 DRAGON", color = NeonRed, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (dragonCard != null) CardView(dragonCard!!) else Box(modifier = Modifier.size(54.dp, 76.dp).background(Color.DarkGray, RoundedCornerShape(8.dp)))
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🐅 TIGER", color = GoldPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (tigerCard != null) CardView(tigerCard!!) else Box(modifier = Modifier.size(54.dp, 76.dp).background(Color.DarkGray, RoundedCornerShape(8.dp)))
                    }
                }
            }

            Text(
                text = resultText,
                color = if (resultText.contains("WON")) NeonGreen else TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            // Side Selection
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("BET SIDE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("DRAGON" to "🐉 Dragon (2x)", "TIE" to "Tie (11x)", "TIGER" to "🐅 Tiger (2x)").forEach { (side, label) ->
                        val isSel = betSide == side
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
                                .clickable { betSide = side }
                                .testTag("dt_side_$side"),
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
                    onClick = { fightDuel() },
                    enabled = !isDealing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("dt_fight"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text(if (isDealing) "FIGHTING..." else "FIGHT DUEL (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

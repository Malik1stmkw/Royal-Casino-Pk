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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CasinoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TeenPattiGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    val suits = listOf("♠️", "♥️", "♦️", "♣️")

    fun drawCard(): PlayingCard {
        val r = ranks.random()
        val v = when (r) {
            "A" -> 14
            "K" -> 13
            "Q" -> 12
            "J" -> 11
            else -> r.toInt()
        }
        return PlayingCard(r, suits.random(), v)
    }

    var betAmount by remember { mutableStateOf(500.0) }
    val myHand = remember { mutableStateListOf<PlayingCard>() }
    var handType by remember { mutableStateOf("Press Deal to play 3-Card Teen Patti!") }
    var isDealing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun playTeenPatti() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            handType = "Insufficient balance!"
            return
        }

        isDealing = true
        myHand.clear()
        handType = "Shuffling cards..."

        scope.launch {
            delay(300)
            myHand.add(drawCard())
            delay(200)
            myHand.add(drawCard())
            delay(200)
            myHand.add(drawCard())

            val r1 = myHand[0].rank
            val r2 = myHand[1].rank
            val r3 = myHand[2].rank

            val s1 = myHand[0].suit
            val s2 = myHand[1].suit
            val s3 = myHand[2].suit

            var multiplier = 0.0
            var name = "High Card"

            if (r1 == r2 && r2 == r3) {
                multiplier = 50.0
                name = "🔥 TRAIL / TRIO (50x)"
            } else if (s1 == s2 && s2 == s3) {
                multiplier = 5.0
                name = "🎨 COLOR / FLUSH (5x)"
            } else if (r1 == r2 || r2 == r3 || r1 == r3) {
                multiplier = 2.0
                name = "👯 PAIR (2x)"
            } else {
                multiplier = 1.2
                name = "🎴 HIGH CARD (1.2x)"
            }

            val payout = betAmount * multiplier
            viewModel.recordGamePlay(9, "Teen Patti 3 Card", betAmount, payout)
            handType = "🎉 $name! WON Rs. ${String.format("%.0f", payout)}"

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("tp_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("🎴 Teen Patti 3 Card", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Trail, Pure Sequence, Pair & High Card", color = TextSecondary, fontSize = 11.sp)
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
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("YOUR 3 CARDS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        myHand.forEach { CardView(it) }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = handType,
                        color = if (handType.contains("WON")) NeonGreen else GoldPrimary,
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
                    onClick = { playTeenPatti() },
                    enabled = !isDealing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("tp_deal"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text(if (isDealing) "DEALING..." else "DEAL TEEN PATTI (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

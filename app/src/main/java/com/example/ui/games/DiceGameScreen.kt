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
fun DiceGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    val diceEmojis = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")
    var betType by remember { mutableStateOf("OVER7") } // OVER7, UNDER7, EXACT7, DOUBLE
    var betAmount by remember { mutableStateOf(500.0) }
    var die1 by remember { mutableStateOf(3) }
    var die2 by remember { mutableStateOf(4) }
    var isRolling by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("Bet Over 7, Under 7, Exact 7 or Doubles!") }

    val scope = rememberCoroutineScope()

    fun rollDice() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            resultMessage = "Insufficient balance!"
            return
        }

        isRolling = true
        resultMessage = "Rolling Dice..."

        scope.launch {
            for (i in 0..12) {
                die1 = (1..6).random()
                die2 = (1..6).random()
                delay(80)
            }

            val d1 = (1..6).random()
            val d2 = (1..6).random()
            die1 = d1
            die2 = d2

            val sum = d1 + d2
            val isDouble = d1 == d2

            var multiplier = 0.0
            when (betType) {
                "OVER7" -> if (sum > 7) multiplier = 2.0
                "UNDER7" -> if (sum < 7) multiplier = 2.0
                "EXACT7" -> if (sum == 7) multiplier = 6.0
                "DOUBLE" -> if (isDouble) multiplier = 6.0
            }

            val payout = betAmount * multiplier
            viewModel.recordGamePlay(11, "Dice Craps", betAmount, payout)

            if (multiplier > 0) {
                resultMessage = "🎉 WON Rs. ${String.format("%.0f", payout)}! Rolled Sum $sum"
            } else {
                resultMessage = "Rolled Sum $sum. Try Again!"
            }

            isRolling = false
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("dice_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("🎲 Dice Craps", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Roll 2 Lucky Dice", color = TextSecondary, fontSize = 11.sp)
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
            // Dice Box
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(diceEmojis[die1 - 1], fontSize = 48.sp, color = Color.Red)
                        }

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(diceEmojis[die2 - 1], fontSize = 48.sp, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "TOTAL SUM: ${die1 + die2}",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = resultMessage,
                        color = if (resultMessage.contains("WON")) NeonGreen else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bet Type Selection
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("BET CONDITION", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("OVER7" to "Over 7 (2x)", "UNDER7" to "Under 7 (2x)", "EXACT7" to "Exact 7 (6x)", "DOUBLE" to "Double (6x)").forEach { (type, label) ->
                        val isSel = betType == type
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
                                .clickable { betType = type }
                                .testTag("dice_bet_$type"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, color = if (isSel) GoldPrimary else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                ChipBetSelector(selectedAmount = betAmount, onAmountSelected = { betAmount = it })

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { rollDice() },
                    enabled = !isRolling,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("roll_dice_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text(if (isRolling) "ROLLING..." else "ROLL DICE (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

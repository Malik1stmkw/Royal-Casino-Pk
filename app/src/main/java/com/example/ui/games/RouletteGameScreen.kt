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
import java.util.Random

@Composable
fun RouletteGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    val redNumbers = setOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
    var betType by remember { mutableStateOf("RED") } // RED, BLACK, EVEN, ODD, 7
    var betAmount by remember { mutableStateOf(500.0) }
    var winningNumber by remember { mutableStateOf<Int?>(null) }
    var isSpinning by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("Place your bet & SPIN!") }

    val scope = rememberCoroutineScope()

    fun spinWheel() {
        if (isSpinning) return
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            resultText = "Insufficient balance!"
            return
        }

        isSpinning = true
        resultText = "Wheel Spinning..."
        val random = Random()

        scope.launch {
            for (i in 0..20) {
                winningNumber = random.nextInt(37)
                com.example.util.SoundEffectsManager.playSpinSound()
                delay(100)
            }

            val resultNum = random.nextInt(37)
            winningNumber = resultNum

            val isRed = redNumbers.contains(resultNum)
            val isEven = resultNum != 0 && resultNum % 2 == 0
            val isOdd = resultNum % 2 != 0

            var multiplier = 0.0
            when (betType) {
                "RED" -> if (isRed) multiplier = 2.0
                "BLACK" -> if (resultNum != 0 && !isRed) multiplier = 2.0
                "EVEN" -> if (isEven) multiplier = 2.0
                "ODD" -> if (isOdd) multiplier = 2.0
                "7" -> if (resultNum == 7) multiplier = 36.0
                "ZERO" -> if (resultNum == 0) multiplier = 36.0
            }

            val payout = betAmount * multiplier
            viewModel.recordGamePlay(2, "European Roulette", betAmount, payout)

            val colorName = if (resultNum == 0) "GREEN" else if (isRed) "RED" else "BLACK"
            if (multiplier > 0) {
                resultText = "🎉 WON Rs. ${String.format("%.0f", payout)}! ($resultNum $colorName)"
            } else {
                resultText = "Landed on $resultNum $colorName. Try Again!"
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("roulette_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("🎡 European Roulette", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Spin 0-36 Wheel", color = TextSecondary, fontSize = 11.sp)
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
            // Wheel Result Box
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
                    val num = winningNumber ?: 0
                    val numColor = if (num == 0) NeonGreen else if (redNumbers.contains(num)) NeonRed else Color.White

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .border(3.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (winningNumber != null) "$num" else "🎡",
                            color = numColor,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = resultText,
                        color = if (resultText.contains("WON")) NeonGreen else TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bet Options Grid
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SELECT BET TYPE",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("RED" to NeonRed, "BLACK" to Color.Black, "EVEN" to Color(0xFF3F51B5), "ODD" to Color(0xFF009688)).forEach { (type, color) ->
                        val isSel = betType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color)
                                .border(
                                    width = if (isSel) 2.dp else 1.dp,
                                    color = if (isSel) GoldPrimary else CasinoCardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { betType = type }
                                .testTag("bet_$type"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$type (2x)",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("7" to "Lucky #7 (36x)", "ZERO" to "Zero #0 (36x)").forEach { (type, label) ->
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
                                .testTag("bet_$type"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = GoldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Bet Selector & Spin Button
            Column(modifier = Modifier.fillMaxWidth()) {
                ChipBetSelector(
                    selectedAmount = betAmount,
                    onAmountSelected = { betAmount = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { spinWheel() },
                    enabled = !isSpinning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("roulette_spin"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (isSpinning) "SPINNING..." else "SPIN ROULETTE (RS. ${betAmount.toInt()})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
fun KenoGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    var betAmount by remember { mutableStateOf(500.0) }
    val selectedNumbers = remember { mutableStateListOf<Int>() }
    val drawnNumbers = remember { mutableStateListOf<Int>() }
    var isDrawing by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("Pick up to 5 numbers (1-40) & Draw!") }

    val scope = rememberCoroutineScope()

    fun toggleNumber(num: Int) {
        if (isDrawing) return
        if (selectedNumbers.contains(num)) {
            selectedNumbers.remove(num)
        } else if (selectedNumbers.size < 5) {
            selectedNumbers.add(num)
        }
    }

    fun playKeno() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            resultText = "Insufficient balance!"
            return
        }
        if (selectedNumbers.isEmpty()) {
            resultText = "Please pick at least 1 number!"
            return
        }

        isDrawing = true
        drawnNumbers.clear()
        resultText = "Drawing Keno Balls..."

        scope.launch {
            val winning = (1..40).shuffled().take(10)
            winning.forEach { n ->
                drawnNumbers.add(n)
                delay(150)
            }

            val matches = selectedNumbers.count { drawnNumbers.contains(it) }
            val multiplier = when (matches) {
                5 -> 100.0
                4 -> 20.0
                3 -> 5.0
                2 -> 2.0
                1 -> 1.0
                else -> 0.0
            }

            val payout = betAmount * multiplier
            viewModel.recordGamePlay(15, "Keno Lucky 80", betAmount, payout)

            if (multiplier > 0) {
                resultText = "🎉 $matches MATCHES! WON Rs. ${String.format("%.0f", payout)} (${multiplier}x)"
            } else {
                resultText = "0 Matches. Try Again!"
            }

            isDrawing = false
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("keno_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("🎱 Keno Lucky 80", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Pick Numbers & Match Drawn Balls", color = TextSecondary, fontSize = 11.sp)
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
            // 40 Number Grid
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CasinoCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(8),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(40) { i ->
                            val num = i + 1
                            val isSel = selectedNumbers.contains(num)
                            val isDrawn = drawnNumbers.contains(num)

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isDrawn && isSel) NeonGreen
                                        else if (isDrawn) Color(0xFF3F51B5)
                                        else if (isSel) GoldPrimary
                                        else CasinoDarkBackground
                                    )
                                    .clickable { toggleNumber(num) }
                                    .testTag("keno_num_$num"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$num",
                                    color = if (isSel || isDrawn) Color.Black else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
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
                ChipBetSelector(selectedAmount = betAmount, onAmountSelected = { betAmount = it })
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { playKeno() },
                    enabled = !isDrawing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("keno_draw"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text(if (isDrawing) "DRAWING BALLS..." else "DRAW KENO (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CasinoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PlinkoGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    val multipliers = listOf(50.0, 10.0, 3.0, 1.5, 0.5, 0.2, 0.5, 1.5, 3.0, 10.0, 50.0)
    var betAmount by remember { mutableStateOf(500.0) }
    var isDropping by remember { mutableStateOf(false) }
    var landedSlot by remember { mutableStateOf<Int?>(null) }
    var resultText by remember { mutableStateOf("Drop Plinko Ball!") }

    val scope = rememberCoroutineScope()

    fun dropBall() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            resultText = "Insufficient balance!"
            return
        }

        isDropping = true
        landedSlot = null
        resultText = "Ball Bouncing Down Pegs..."

        scope.launch {
            delay(1200)
            val slotIdx = (0 until multipliers.size).random()
            landedSlot = slotIdx
            val mult = multipliers[slotIdx]
            val payout = betAmount * mult

            viewModel.recordGamePlay(12, "Plinko Pyramid", betAmount, payout)
            resultText = "🎉 WON Rs. ${String.format("%.0f", payout)}! (${mult}x Slot)"
            isDropping = false
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("plinko_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("📊 Plinko Pyramid", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Peg Grid Multiplier Drop", color = TextSecondary, fontSize = 11.sp)
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
            // Peg Grid Pyramid Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CasinoCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔴 PLINKO BALL", fontSize = 14.sp, color = GoldPrimary)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated Peg Rows
                    repeat(5) { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(row + 3) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Multiplier Buckets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        multipliers.forEachIndexed { idx, m ->
                            val isLanded = landedSlot == idx
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isLanded) GoldPrimary else CasinoDarkBackground)
                                    .padding(horizontal = 4.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${m}x",
                                    color = if (isLanded) Color.Black else TextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
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
                ChipBetSelector(selectedAmount = betAmount, onAmountSelected = { betAmount = it })
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { dropBall() },
                    enabled = !isDropping,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("drop_plinko_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text(if (isDropping) "DROPPING..." else "DROP BALL (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

package com.example.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Brush
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
fun AviatorGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    var betAmount by remember { mutableStateOf(500.0) }
    var multiplier by remember { mutableStateOf(1.00) }
    var isFlying by remember { mutableStateOf(false) }
    var isCashedOut by remember { mutableStateOf(false) }
    var crashPoint by remember { mutableStateOf(1.00) }
    var statusText by remember { mutableStateOf("Place Bet & Launch Rocket!") }

    val scope = rememberCoroutineScope()

    fun launchRocket() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            statusText = "Insufficient balance!"
            return
        }

        isFlying = true
        isCashedOut = false
        multiplier = 1.00

        // Determine random crash point between 1.1x and 25x
        val rand = Math.random()
        crashPoint = if (rand < 0.5) 1.2 + Math.random() * 1.5 else if (rand < 0.8) 2.5 + Math.random() * 5.0 else 7.0 + Math.random() * 18.0

        statusText = "🚀 Rocket Flying..."

        scope.launch {
            var currentMult = 1.00
            while (currentMult < crashPoint && isFlying && !isCashedOut) {
                delay(100)
                currentMult += (0.02 + currentMult * 0.03)
                multiplier = currentMult
            }

            if (!isCashedOut && isFlying) {
                // Flew away / crashed!
                isFlying = false
                viewModel.recordGamePlay(7, "Aviator Crash", betAmount, 0.0)
                statusText = "💥 FLEW AWAY at ${String.format("%.2f", crashPoint)}x! Lost Rs. ${betAmount.toInt()}"
            }
        }
    }

    fun cashOut() {
        if (!isFlying || isCashedOut) return
        isCashedOut = true
        isFlying = false
        val winMult = multiplier
        val payout = betAmount * winMult
        viewModel.recordGamePlay(7, "Aviator Crash", betAmount, payout)
        statusText = "🎉 CASHED OUT at ${String.format("%.2f", winMult)}x! WON Rs. ${String.format("%.0f", payout)}"
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("aviator_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("🚀 Aviator Crash", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Cash Out before Rocket Crashes", color = TextSecondary, fontSize = 11.sp)
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
            // Rocket Flight Arena Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .border(
                        1.dp,
                        Brush.verticalGradient(listOf(GoldPrimary, CasinoCardBorder)),
                        RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF22114A), Color(0xFF090514))
                            )
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isFlying) "🚀" else "💥",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "${String.format("%.2f", multiplier)}x",
                            color = if (isCashedOut) NeonGreen else if (!isFlying && multiplier > 1.0) NeonRed else GoldPrimary,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Text(
                text = statusText,
                color = if (statusText.contains("CASHED OUT")) NeonGreen else if (statusText.contains("FLEW")) NeonRed else TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            // Bet Controls
            Column(modifier = Modifier.fillMaxWidth()) {
                if (isFlying && !isCashedOut) {
                    Button(
                        onClick = { cashOut() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .testTag("aviator_cashout"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "CASH OUT (RS. ${String.format("%.0f", betAmount * multiplier)})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                } else {
                    ChipBetSelector(selectedAmount = betAmount, onAmountSelected = { betAmount = it })
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { launchRocket() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("aviator_launch"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("LAUNCH ROCKET (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

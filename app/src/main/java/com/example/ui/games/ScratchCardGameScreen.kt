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

data class ScratchTile(val symbol: String, var isScratched: Boolean = false)

@Composable
fun ScratchCardGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    val symbols = listOf("👑", "💎", "🪙", "🔔", "🍇")
    var betAmount by remember { mutableStateOf(500.0) }
    var isCardActive by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("Buy Scratch Card & match 3 symbols!") }

    val tiles = remember {
        mutableStateListOf<ScratchTile>().apply {
            repeat(9) { add(ScratchTile("❓")) }
        }
    }

    fun buyCard() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            resultText = "Insufficient balance!"
            return
        }

        tiles.clear()
        val generated = List(9) { symbols.random() }
        generated.forEach { tiles.add(ScratchTile(it, false)) }

        isCardActive = true
        resultText = "Scratch tiles to reveal hidden symbols!"
    }

    fun scratchTile(idx: Int) {
        if (!isCardActive || tiles[idx].isScratched) return
        tiles[idx] = tiles[idx].copy(isScratched = true)

        if (tiles.all { it.isScratched }) {
            // Check matching 3
            val counts = tiles.groupingBy { it.symbol }.eachCount()
            var maxMult = 0.0
            counts.forEach { (sym, count) ->
                if (count >= 3) {
                    val m = when (sym) {
                        "👑" -> 500.0
                        "💎" -> 100.0
                        "🪙" -> 10.0
                        "🔔" -> 5.0
                        else -> 2.0
                    }
                    if (m > maxMult) maxMult = m
                }
            }

            val payout = betAmount * maxMult
            viewModel.recordGamePlay(13, "Scratch Gold Card", betAmount, payout)
            isCardActive = false

            if (maxMult > 0) {
                resultText = "🎉 WON RS. ${String.format("%.0f", payout)} (${maxMult}x Match)!"
            } else {
                resultText = "No 3 matching symbols. Try Again!"
            }
        }
    }

    fun scratchAll() {
        if (!isCardActive) return
        tiles.indices.forEach { scratchTile(it) }
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("scratch_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("🎟️ Scratch Gold Card", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Match 3 Gold Symbols to Win", color = TextSecondary, fontSize = 11.sp)
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
            // 3x3 Card
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
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(9) { idx ->
                            val tile = tiles[idx]
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (tile.isScratched) Color.Black else GoldPrimary)
                                    .border(1.dp, CasinoCardBorder, RoundedCornerShape(12.dp))
                                    .clickable { scratchTile(idx) }
                                    .testTag("scratch_tile_$idx"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (tile.isScratched) tile.symbol else "🪙",
                                    fontSize = 28.sp
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
                if (isCardActive) {
                    Button(
                        onClick = { scratchAll() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("scratch_reveal_all"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("REVEAL ALL TILES", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    ChipBetSelector(selectedAmount = betAmount, onAmountSelected = { betAmount = it })
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { buyCard() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("buy_scratch_card"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("BUY SCRATCH CARD (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

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
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CasinoViewModel

data class TileState(
    val index: Int,
    var isBomb: Boolean = false,
    var isRevealed: Boolean = false
)

@Composable
fun MinesGameScreen(
    viewModel: CasinoViewModel,
    onBack: () -> Unit
) {
    var betAmount by remember { mutableStateOf(500.0) }
    var bombCount by remember { mutableStateOf(3) }
    var isGameActive by remember { mutableStateOf(false) }
    var revealedCount by remember { mutableStateOf(0) }
    var currentMultiplier by remember { mutableStateOf(1.0) }
    var resultText by remember { mutableStateOf("Start Game & uncover safe Gems!") }

    val tiles = remember {
        mutableStateListOf<TileState>().apply {
            repeat(25) { add(TileState(it)) }
        }
    }

    fun startGame() {
        val balance = viewModel.userProfile.value?.balance ?: 0.0
        if (balance < betAmount) {
            resultText = "Insufficient balance!"
            return
        }

        // Reset tiles
        tiles.forEachIndexed { i, t ->
            tiles[i] = TileState(i)
        }

        // Randomly place bombs
        val indices = (0..24).shuffled().take(bombCount)
        indices.forEach { idx ->
            tiles[idx] = tiles[idx].copy(isBomb = true)
        }

        isGameActive = true
        revealedCount = 0
        currentMultiplier = 1.0
        resultText = "Tap safe tiles!"
    }

    fun revealTile(index: Int) {
        if (!isGameActive) return
        val tile = tiles[index]
        if (tile.isRevealed) return

        if (tile.isBomb) {
            // Hit Bomb!
            tiles[index] = tile.copy(isRevealed = true)
            // Reveal all bombs
            tiles.forEachIndexed { i, t ->
                if (t.isBomb) tiles[i] = t.copy(isRevealed = true)
            }
            isGameActive = false
            viewModel.recordGamePlay(6, "Mines Sweeper", betAmount, 0.0)
            resultText = "💥 BOMB HIT! Lost Rs. ${betAmount.toInt()}"
        } else {
            // Uncovered safe Gem
            tiles[index] = tile.copy(isRevealed = true)
            revealedCount++
            currentMultiplier = 1.0 + (revealedCount * (0.25 * bombCount))
            resultText = "Gem found! Multiplier: ${String.format("%.2f", currentMultiplier)}x"
        }
    }

    fun cashOut() {
        if (!isGameActive || revealedCount == 0) return
        val payout = betAmount * currentMultiplier
        isGameActive = false
        // Reveal remaining bombs
        tiles.forEachIndexed { i, t ->
            if (t.isBomb) tiles[i] = t.copy(isRevealed = true)
        }

        viewModel.recordGamePlay(6, "Mines Sweeper", betAmount, payout)
        resultText = "🎉 CASHED OUT Rs. ${String.format("%.0f", payout)} (${String.format("%.2f", currentMultiplier)}x)!"
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("mines_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("💣 Mines Sweeper", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Uncover Gems & Cash Out Anytime", color = TextSecondary, fontSize = 11.sp)
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
            // Status Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CasinoSurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("MULTIPLIER", color = TextSecondary, fontSize = 10.sp)
                        Text("${String.format("%.2f", currentMultiplier)}x", color = GoldPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("WIN AMOUNT", color = TextSecondary, fontSize = 10.sp)
                        Text("Rs. ${String.format("%.0f", betAmount * currentMultiplier)}", color = NeonGreen, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            // 5x5 Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(25) { index ->
                    val tile = tiles[index]
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (tile.isRevealed) {
                                    if (tile.isBomb) NeonRed.copy(alpha = 0.3f) else NeonGreen.copy(alpha = 0.3f)
                                } else CasinoSurfaceDark
                            )
                            .border(
                                1.dp,
                                if (tile.isRevealed) GoldPrimary else CasinoCardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { revealTile(index) }
                            .testTag("mines_tile_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (tile.isRevealed) (if (tile.isBomb) "💣" else "💎") else "❓",
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Text(
                text = resultText,
                color = if (resultText.contains("CASHED OUT")) NeonGreen else if (resultText.contains("BOMB")) NeonRed else TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            // Controls
            Column(modifier = Modifier.fillMaxWidth()) {
                if (isGameActive) {
                    Button(
                        onClick = { cashOut() },
                        enabled = revealedCount > 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("mines_cashout"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black)
                    ) {
                        Text("CASH OUT (RS. ${String.format("%.0f", betAmount * currentMultiplier)})", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }
                } else {
                    ChipBetSelector(selectedAmount = betAmount, onAmountSelected = { betAmount = it })
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { startGame() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("mines_start"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("START MINES (RS. ${betAmount.toInt()})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

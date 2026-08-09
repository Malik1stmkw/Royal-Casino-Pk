package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.theme.FrostedIndigo
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CasinoViewModel

data class MultiplayerTable(
    val id: Int,
    val name: String,
    val gameName: String,
    val playersCount: Int,
    val minBet: Double
)

data class ChatMessage(val sender: String, val text: String, val time: String)

@Composable
fun MultiplayerScreen(
    viewModel: CasinoViewModel,
    onGameSelect: (Int) -> Unit
) {
    val liveTicker by viewModel.liveTicker.collectAsState()

    val tables = listOf(
        MultiplayerTable(1, "VIP Royal Teen Patti", "Teen Patti 3 Card", 5, 1000.0),
        MultiplayerTable(2, "High Rollers Blackjack", "Blackjack 21", 4, 2000.0),
        MultiplayerTable(3, "European Roulette Pro", "Roulette Royale", 12, 500.0),
        MultiplayerTable(4, "Aviator Rocket Squad", "Aviator Crash", 38, 500.0),
        MultiplayerTable(5, "Dragon vs Tiger Duel", "Dragon vs Tiger", 18, 500.0),
        MultiplayerTable(6, "Baccarat Millionaires", "Baccarat Royale", 6, 5000.0)
    )

    var chatInput by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("Ali_Lahore", "Good luck everyone at the table!", "10:12 AM"),
            ChatMessage("Hamza_KHI", "Just won Rs. 45,000 on Aviator!! 🚀", "10:14 AM"),
            ChatMessage("Zeeshan Graphics", "Welcome to Royal Casino 16-in-1!", "10:15 AM")
        )
    }

    fun sendMessage() {
        if (chatInput.isNotBlank()) {
            messages.add(ChatMessage("Zeeshan Graphics", chatInput, "Just now"))
            chatInput = ""
        }
    }

    FrostedGlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Group, contentDescription = "Multiplayer", tint = GoldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LIVE MULTIPLAYER ROOMS", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .background(NeonGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("🟢 1,420 ONLINE", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lobbies
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tables) { table ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                            .clickable { onGameSelect(table.id) }
                            .testTag("mp_table_${table.id}"),
                        colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(table.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("${table.gameName} • Min Bet Rs. ${table.minBet.toInt()}", color = TextSecondary, fontSize = 11.sp)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👥 ${table.playersCount}", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Brush.horizontalGradient(listOf(FrostedIndigo, Color(0xFF8B5CF6))),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("JOIN", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chat Feed Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("💬 LIVE MULTIPLAYER TABLE CHAT", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                    LazyColumn(
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(messages) { msg ->
                            Row {
                                Text("${msg.sender}: ", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(msg.text, color = TextPrimary, fontSize = 11.sp)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("Type chat message...", color = TextSecondary, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).height(44.dp).testTag("mp_chat_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0x33FFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(FrostedIndigo)
                                .clickable { sendMessage() }
                                .testTag("mp_chat_send"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}


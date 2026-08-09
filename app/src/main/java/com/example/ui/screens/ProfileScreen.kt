package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.ui.components.SettingsPanel
import com.example.ui.components.VipLoyaltyCard

@Composable
fun ProfileScreen(
    viewModel: CasinoViewModel
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val stats by viewModel.gameStats.collectAsState()

    val totalPlayed = stats.sumOf { it.timesPlayed }
    val totalWon = stats.sumOf { it.totalPayout }

    FrostedGlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(FrostedIndigo, Color(0xFF8B5CF6)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(userProfile?.username ?: "Zeeshan Graphics", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("VIP LEVEL ${userProfile?.vipLevel ?: 1} MEMBER", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x1FFFFFFF), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = "Email", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Email: ", color = TextSecondary, fontSize = 12.sp)
                            Text(userProfile?.email ?: "zeeshangraphicsmkw@gmail.com", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = "Account", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("EasyPaisa Number: ", color = TextSecondary, fontSize = 12.sp)
                            Text(userProfile?.easypaisaNumber ?: "03490802208", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("TOTAL GAMES", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("$totalPlayed", color = GoldPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("TOTAL WINNINGS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("Rs. ${String.format("%.0f", totalWon)}", color = NeonGreen, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            // VIP Loyalty Program Card (DataStore Persisted)
            VipLoyaltyCard(viewModel = viewModel)

            // Settings Panel (DataStore Persisted)
            SettingsPanel(viewModel = viewModel)

            // Security Badge
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = "Security", tint = NeonGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("SSL Secure Encrypted Transactions", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Instant EasyPaisa & JazzCash Gateway", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}


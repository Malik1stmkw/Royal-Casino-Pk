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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.theme.FrostedIndigo
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CasinoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    viewModel: CasinoViewModel
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var activeTab by remember { mutableStateOf("DEPOSIT") } // DEPOSIT, WITHDRAW, HISTORY
    var methodSelected by remember { mutableStateOf("EASYPAISA") } // EASYPAISA, JAZZCASH

    var depositAmountText by remember { mutableStateOf("1000") }
    var depositTrxIdText by remember { mutableStateOf("EP789456123") }
    var depositAccountText by remember { mutableStateOf(userProfile?.easypaisaNumber ?: "03490802208") }

    var withdrawAmountText by remember { mutableStateOf("2000") }
    var withdrawAccountText by remember { mutableStateOf(userProfile?.easypaisaNumber ?: "03490802208") }

    FrostedGlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Balance Header Card (Frosted Glass Container)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0x40FFFFFF), Color(0x1AFFFFFF))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x2912172A))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CURRENT WALLET BALANCE",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Rs. ${String.format("%.2f", userProfile?.balance ?: 10000.0)}",
                        color = GoldPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("USER EMAIL", color = TextSecondary, fontSize = 9.sp)
                            Text(userProfile?.email ?: "zeeshangraphicsmkw@gmail.com", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ACCOUNT NO.", color = TextSecondary, fontSize = 9.sp)
                            Text(userProfile?.easypaisaNumber ?: "03490802208", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("QUICK SIMULATION", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonGreen.copy(alpha = 0.2f))
                                .border(1.dp, NeonGreen, RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.depositEasyPaisa(1000.0, userProfile?.easypaisaNumber ?: "03490802208", "EP${System.currentTimeMillis() % 1000000}")
                                }
                                .testTag("quick_ep_deposit"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🟢 +1k EasyPaisa", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x33EF4444))
                                .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.depositJazzCash(1000.0, userProfile?.easypaisaNumber ?: "03490802208", "JC${System.currentTimeMillis() % 1000000}")
                                }
                                .testTag("quick_jc_deposit"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔴 +1k JazzCash", color = Color(0xFFF87171), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x1FFFFFFF))
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.withdrawEasyPaisa(500.0, userProfile?.easypaisaNumber ?: "03490802208")
                                }
                                .testTag("quick_ep_withdraw"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📤 -500 EP", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x1FFFFFFF))
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.withdrawJazzCash(500.0, userProfile?.easypaisaNumber ?: "03490802208")
                                }
                                .testTag("quick_jc_withdraw"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📤 -500 JC", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wallet Action Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("DEPOSIT" to "📥 Deposit", "WITHDRAW" to "📤 Withdraw", "HISTORY" to "📜 History").forEach { (tab, label) ->
                    val isSel = activeTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSel) Brush.horizontalGradient(listOf(FrostedIndigo, Color(0xFF8B5CF6)))
                                else Brush.horizontalGradient(listOf(Color(0x1FFFFFFF), Color(0x1AFFFFFF)))
                            )
                            .border(1.dp, if (isSel) Color(0x806366F1) else Color(0x26FFFFFF), RoundedCornerShape(14.dp))
                            .clickable { activeTab = tab }
                            .testTag("wallet_tab_$tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSel) Color.White else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (activeTab) {
                "DEPOSIT" -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("SELECT PAYMENT METHOD", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                listOf("EASYPAISA" to "🟢 EasyPaisa (03490802208)", "JAZZCASH" to "🔴 JazzCash (03490802208)").forEach { (m, label) ->
                                    val isM = methodSelected == m
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0x1FFFFFFF))
                                            .border(
                                                width = if (isM) 2.dp else 1.dp,
                                                color = if (isM) GoldPrimary else Color(0x26FFFFFF),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { methodSelected = m }
                                            .testTag("method_$m"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(label, color = if (isM) GoldPrimary else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = depositAmountText,
                                onValueChange = { depositAmountText = it },
                                label = { Text("Deposit Amount (PKR)", color = TextSecondary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("deposit_amount_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0x33FFFFFF),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = depositAccountText,
                                onValueChange = { depositAccountText = it },
                                label = { Text("EasyPaisa/JazzCash Account Number", color = TextSecondary) },
                                modifier = Modifier.fillMaxWidth().testTag("deposit_account_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0x33FFFFFF),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = depositTrxIdText,
                                onValueChange = { depositTrxIdText = it },
                                label = { Text("Transaction Reference ID / Trx ID", color = TextSecondary) },
                                modifier = Modifier.fillMaxWidth().testTag("deposit_trx_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0x33FFFFFF),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val amt = depositAmountText.toDoubleOrNull() ?: 0.0
                                    if (methodSelected == "JAZZCASH") {
                                        viewModel.depositJazzCash(amt, depositAccountText, depositTrxIdText)
                                    } else {
                                        viewModel.depositEasyPaisa(amt, depositAccountText, depositTrxIdText)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("submit_deposit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("CONFIRM INSTANT $methodSelected DEPOSIT", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                "WITHDRAW" -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("SELECT WITHDRAWAL METHOD", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                listOf("EASYPAISA" to "🟢 EasyPaisa", "JAZZCASH" to "🔴 JazzCash").forEach { (m, label) ->
                                    val isM = methodSelected == m
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0x1FFFFFFF))
                                            .border(
                                                width = if (isM) 2.dp else 1.dp,
                                                color = if (isM) GoldPrimary else Color(0x26FFFFFF),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { methodSelected = m }
                                            .testTag("withdraw_method_$m"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(label, color = if (isM) GoldPrimary else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = withdrawAmountText,
                                onValueChange = { withdrawAmountText = it },
                                label = { Text("Withdrawal Amount (Min Rs. 500)", color = TextSecondary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("withdraw_amount_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0x33FFFFFF),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = withdrawAccountText,
                                onValueChange = { withdrawAccountText = it },
                                label = { Text("$methodSelected Account Number", color = TextSecondary) },
                                modifier = Modifier.fillMaxWidth().testTag("withdraw_account_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0x33FFFFFF),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val amt = withdrawAmountText.toDoubleOrNull() ?: 0.0
                                    if (methodSelected == "JAZZCASH") {
                                        viewModel.withdrawJazzCash(amt, withdrawAccountText)
                                    } else {
                                        viewModel.withdrawEasyPaisa(amt, withdrawAccountText)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("submit_withdraw_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("SUBMIT $methodSelected WITHDRAWAL REQUEST", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                "HISTORY" -> {
                    if (transactions.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No transaction history yet.", color = TextSecondary)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(transactions) { trx ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(16.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (trx.amount >= 0) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                                contentDescription = trx.type,
                                                tint = if (trx.amount >= 0) NeonGreen else NeonRed
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(trx.note, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(trx.timestamp))
                                                Text(dateStr, color = TextSecondary, fontSize = 10.sp)
                                            }
                                        }

                                        Text(
                                            text = "${if (trx.amount >= 0) "+" else ""}Rs. ${String.format("%.0f", trx.amount)}",
                                            color = if (trx.amount >= 0) NeonGreen else NeonRed,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


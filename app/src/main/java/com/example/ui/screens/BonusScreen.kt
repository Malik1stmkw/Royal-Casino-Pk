package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.components.VipLoyaltyCard
import com.example.ui.theme.FrostedIndigo
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.SoundEffectsManager
import com.example.viewmodel.CasinoViewModel
import kotlinx.coroutines.launch

@Composable
fun BonusScreen(
    viewModel: CasinoViewModel
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val vipState by viewModel.vipLoyaltyState.collectAsState()
    val rewardsList = listOf(500.0, 1000.0, 2000.0, 3500.0, 5000.0, 7500.0, 15000.0)

    val currentStreak = userProfile?.dailyStreak ?: 1
    val targetStreakProgress = (currentStreak.coerceIn(1, 7) / 7f)
    val animatedStreakProgress by animateFloatAsState(
        targetValue = targetStreakProgress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "StreakProgressBarAnim"
    )

    // Sign-Up Bonus Milestone state
    var claimedMilestones by remember { mutableIntStateOf(2) } // Default 2/4 completed
    val milestoneTargetProgress = (claimedMilestones / 4f)
    val animatedMilestoneProgress by animateFloatAsState(
        targetValue = milestoneTargetProgress,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "MilestoneProgressBarAnim"
    )

    var luckySpinMsg by remember { mutableStateOf("Spin the Wheel of Fortune!") }
    var isSpinning by remember { mutableStateOf(false) }

    val rotationAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun triggerLuckySpin() {
        if (isSpinning) return
        isSpinning = true
        SoundEffectsManager.playSpinSound()
        luckySpinMsg = "Spinning Wheel..."

        val prizeOptions = listOf(1000.0, 2500.0, 5000.0, 10000.0, 25000.0, 50000.0)
        val selectedPrize = prizeOptions.random()
        val index = prizeOptions.indexOf(selectedPrize)
        val targetRot = 360f * 5 + (index * (360f / 6f))

        scope.launch {
            rotationAnim.animateTo(
                targetValue = targetRot,
                animationSpec = tween(durationMillis = 3000)
            )

            viewModel.recordGamePlay(0, "Lucky Wheel Bonus", 0.0, selectedPrize)
            SoundEffectsManager.playJackpotSound()
            luckySpinMsg = "🎉 LUCKY WIN! Rs. ${selectedPrize.toInt()} Free Chips Credited!"
            isSpinning = false
        }
    }

    FrostedGlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(FrostedIndigo, Color(0xFF8B5CF6)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "Bonus", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("SIGN-UP WELCOME BONUS", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Rs. 10,000 Free Balance Credited", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            // VIP Loyalty Program Card with Multiplier & Animated XP Progress
            VipLoyaltyCard(viewModel = viewModel)

            // Sign-Up Bonus Milestones Card with Animated Progress Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                    .testTag("signup_bonus_milestones_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("WELCOME REWARD MILESTONES", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("$claimedMilestones of 4 Milestones Unlocked", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Box(
                            modifier = Modifier
                                .background(GoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .border(1.dp, GoldPrimary, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("${(animatedMilestoneProgress * 100).toInt()}% DONE", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Animated Progress Bar for Milestones
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33FFFFFF))
                            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(10.dp))
                            .testTag("milestones_animated_progress_bar")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedMilestoneProgress)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(GoldPrimary, NeonCyan, NeonGreen)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Milestone Items
                    val milestonesList = listOf(
                        Triple("Registration Welcome Bonus", "Rs. 1,000", 1 <= claimedMilestones),
                        Triple("Link EasyPaisa/JazzCash Account", "Rs. 2,500", 2 <= claimedMilestones),
                        Triple("Place First Casino Wager", "Rs. 3,000", 3 <= claimedMilestones),
                        Triple("Complete 3-Day Login Streak", "Rs. 3,500", 4 <= claimedMilestones)
                    )

                    milestonesList.forEachIndexed { idx, (title, reward, isUnlocked) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isUnlocked) Color(0x1A10B981) else Color(0x0FFFFFFF))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isUnlocked) NeonGreen else Color(0x33FFFFFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isUnlocked) Icons.Default.Check else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (isUnlocked) Color.Black else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(title, color = if (isUnlocked) TextPrimary else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Text(reward, color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (claimedMilestones < 4) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (claimedMilestones < 4) {
                                    claimedMilestones += 1
                                    SoundEffectsManager.playJackpotSound()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("claim_next_milestone_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("CLAIM NEXT MILESTONE BONUS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // 7 Day Daily Bonus Grid & Animated Streak Progress Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                    .testTag("daily_streak_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("7-DAY DAILY LOGIN STREAK", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Day $currentStreak of 7 Active", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${(animatedStreakProgress * 100).toInt()}%", color = NeonGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Animated Progress Bar for Daily Streak
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33FFFFFF))
                            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(10.dp))
                            .testTag("streak_animated_progress_bar")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedStreakProgress)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(NeonGreen, NeonCyan, GoldPrimary)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7 Days Display Grid
                    val firstRowDays = rewardsList.take(4)
                    val secondRowDays = rewardsList.drop(4)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            firstRowDays.forEachIndexed { index, amount ->
                                val dayNum = index + 1
                                val isClaimed = currentStreak > dayNum
                                val isCurrent = currentStreak == dayNum

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            when {
                                                isClaimed -> NeonGreen.copy(alpha = 0.2f)
                                                isCurrent -> GoldPrimary.copy(alpha = 0.25f)
                                                else -> Color(0x1FFFFFFF)
                                            }
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = when {
                                                isClaimed -> NeonGreen
                                                isCurrent -> GoldPrimary
                                                else -> Color(0x26FFFFFF)
                                            },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("DAY $dayNum", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text("Rs. ${amount.toInt()}", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                        if (isClaimed) {
                                            Text("✔", color = NeonGreen, fontSize = 11.sp)
                                        } else if (isCurrent) {
                                            Text("TODAY", color = GoldPrimary, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            secondRowDays.forEachIndexed { index, amount ->
                                val dayNum = index + 5
                                val isClaimed = currentStreak > dayNum
                                val isCurrent = currentStreak == dayNum

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            when {
                                                isClaimed -> NeonGreen.copy(alpha = 0.2f)
                                                isCurrent -> GoldPrimary.copy(alpha = 0.25f)
                                                else -> Color(0x1FFFFFFF)
                                            }
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = when {
                                                isClaimed -> NeonGreen
                                                isCurrent -> GoldPrimary
                                                else -> Color(0x26FFFFFF)
                                            },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("DAY $dayNum", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text("Rs. ${amount.toInt()}", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                        if (isClaimed) {
                                            Text("✔", color = NeonGreen, fontSize = 11.sp)
                                        } else if (isCurrent) {
                                            Text("TODAY", color = GoldPrimary, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.claimDailyBonus { _ -> }
                            SoundEffectsManager.playJackpotSound()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("claim_daily_bonus_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "CLAIM TODAY'S REWARD (${vipState.multiplier}x VIP MULTIPLIER)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Lucky Wheel Module
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0x2912172A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("LUCKY SPIN WHEEL", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .rotate(rotationAnim.value)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(GoldPrimary, NeonCyan, NeonPurple, NeonGreen, NeonRed)
                                )
                            )
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎁 SPIN", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(luckySpinMsg, color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { triggerLuckySpin() },
                        enabled = !isSpinning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("spin_lucky_wheel_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = FrostedIndigo, contentColor = Color.White),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(if (isSpinning) "SPINNING..." else "SPIN FREE WHEEL", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}



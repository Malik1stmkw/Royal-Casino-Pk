package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.VipLoyaltyState
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.SoundEffectsManager
import com.example.viewmodel.CasinoViewModel

@Composable
fun VipLoyaltyCard(
    viewModel: CasinoViewModel,
    modifier: Modifier = Modifier
) {
    val vipState by viewModel.vipLoyaltyState.collectAsState()

    // Shimmer/Rotation Animation
    val infiniteTransition = rememberInfiniteTransition(label = "VipCardShimmer")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "VipBadgeRotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "VipPulseScale"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = vipState.progressFraction,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "VipXpProgressAnim"
    )

    // Tier specific colors
    val (primaryTierColor, secondaryTierColor, tierGradient) = getTierThemeColors(vipState.tierLevel)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                brush = Brush.sweepGradient(
                    listOf(primaryTierColor, secondaryTierColor, GoldPrimary, primaryTierColor)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .testTag("vip_loyalty_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x3312172A))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header Row with VIP Badge and Multiplier
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(primaryTierColor.copy(alpha = 0.5f), Color(0x11000000))
                                )
                            )
                            .border(2.dp, primaryTierColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = vipState.iconSymbol,
                            fontSize = 26.sp,
                            modifier = Modifier.rotate(rotationAngle * 0.1f)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = vipState.currentTierName.uppercase(),
                                color = primaryTierColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "VIP Status",
                                tint = GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Level ${vipState.tierLevel} VIP Loyalty Status",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Multiplier Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(primaryTierColor.copy(alpha = 0.25f), GoldPrimary.copy(alpha = 0.2f))
                            )
                        )
                        .border(1.dp, primaryTierColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${vipState.multiplier}x MULTIPLIER",
                            color = primaryTierColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Daily Bonus Boost",
                            color = TextSecondary,
                            fontSize = 8.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // XP Progress Bar Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOYALTY XP PROGRESS",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${vipState.xp} / ${vipState.nextTierXpTarget} XP",
                    color = GoldPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Animated Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(10.dp))
                    .testTag("vip_xp_progress_bar")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(primaryTierColor, GoldPrimary, NeonGreen)
                            )
                        )
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}% TO NEXT VIP TIER",
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // VIP Loyalty Perks List
            Text(
                text = "EXCLUSIVE VIP BENEFITS & PERKS",
                color = GoldPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            val perks = getVipPerksForLevel(vipState.tierLevel)
            perks.forEach { perk ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x1FFFFFFF))
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = perk.icon,
                        contentDescription = null,
                        tint = primaryTierColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = perk.title,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = perk.description,
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Level Up Claim Button / Status
            val rewardChips = vipState.tierLevel * 10000.0
            if (vipState.claimedRewardLevel < vipState.tierLevel) {
                Button(
                    onClick = {
                        viewModel.claimVipLevelReward(vipState.tierLevel, rewardChips)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("claim_vip_reward_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "🎉 CLAIM TIER ${vipState.tierLevel} UNLOCK REWARD: Rs. ${rewardChips.toInt()} FREE CHIPS",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonGreen.copy(alpha = 0.15f))
                        .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✔ VIP TIER ${vipState.tierLevel} REWARDS ACTIVE & SAVED IN DATASTORE",
                        color = NeonGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private data class VipPerkItem(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private fun getVipPerksForLevel(tierLevel: Int): List<VipPerkItem> {
    return listOf(
        VipPerkItem(
            icon = Icons.Default.WorkspacePremium,
            title = "Daily Bonus Multiplier",
            description = "${when(tierLevel){ 1 -> "1.0x"; 2 -> "1.25x"; 3 -> "1.50x"; 4 -> "2.0x"; 5 -> "2.5x"; else -> "3.0x" }} payout on daily login streak chips"
        ),
        VipPerkItem(
            icon = Icons.Default.Stars,
            title = "Exclusive Tier Avatar Frame & Badge",
            description = "Custom glowing avatar border in game rooms and high-roller tables"
        ),
        VipPerkItem(
            icon = Icons.Default.Diamond,
            title = "Free Lucky Wheel Spins",
            description = "Bonus free spin tokens awarded automatically on game bets"
        ),
        VipPerkItem(
            icon = Icons.Default.MilitaryTech,
            title = "Priority Cash Withdrawals",
            description = "Instant EasyPaisa / JazzCash payout dispatch with zero fees"
        )
    )
}

private fun getTierThemeColors(tierLevel: Int): Triple<Color, Color, List<Color>> {
    return when (tierLevel) {
        1 -> Triple(Color(0xFFCD7F32), Color(0xFF8B5A2B), listOf(Color(0xFFCD7F32), Color(0xFF8B5A2B))) // Bronze
        2 -> Triple(Color(0xFFC0C0C0), Color(0xFFE0E0E0), listOf(Color(0xFFC0C0C0), NeonCyan)) // Silver
        3 -> Triple(GoldPrimary, Color(0xFFFFB300), listOf(GoldPrimary, Color(0xFFFFB300))) // Gold
        4 -> Triple(NeonCyan, Color(0xFF00B0FF), listOf(NeonCyan, NeonPurple)) // Platinum
        5 -> Triple(NeonPurple, NeonRed, listOf(NeonPurple, GoldPrimary)) // Diamond
        else -> Triple(Color(0xFFFF007F), GoldPrimary, listOf(Color(0xFFFF007F), GoldPrimary, NeonGreen)) // Royal Dragon
    }
}

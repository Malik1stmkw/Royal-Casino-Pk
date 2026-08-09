package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserSettings
import com.example.ui.theme.FrostedIndigo
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.SoundEffectsManager
import com.example.viewmodel.CasinoViewModel

@Composable
fun SettingsPanel(
    viewModel: CasinoViewModel,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color(0x66FFFFFF), Color(0x1AFFFFFF))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .testTag("settings_panel_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x2912172A))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldPrimary.copy(alpha = 0.2f))
                            .border(1.dp, GoldPrimary, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Settings",
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("APP PREFERENCES", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Saved locally with Android DataStore", color = TextSecondary, fontSize = 10.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .background(NeonGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("DATASTORE ACTIVE", color = NeonGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Music Toggle Item
            SettingToggleItem(
                icon = Icons.Default.MusicNote,
                iconTint = NeonCyan,
                title = "Background Music",
                description = "Play ambient casino lounge music soundtrack",
                isChecked = userSettings.musicEnabled,
                onCheckedChange = {
                    SoundEffectsManager.playButtonClick()
                    viewModel.updateMusicEnabled(it)
                },
                testTag = "setting_toggle_music"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sound Effects Toggle Item
            SettingToggleItem(
                icon = if (userSettings.soundEffectsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                iconTint = GoldPrimary,
                title = "Sound Effects & Fanfare",
                description = "Game spin tones, button clicks, & win fanfare",
                isChecked = userSettings.soundEffectsEnabled,
                onCheckedChange = {
                    viewModel.updateSoundEffectsEnabled(it)
                    if (it) {
                        SoundEffectsManager.playWinSound()
                    }
                },
                testTag = "setting_toggle_sound_effects"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notification Toggle Item
            SettingToggleItem(
                icon = Icons.Default.Notifications,
                iconTint = NeonGreen,
                title = "Push Notifications",
                description = "Daily streak rewards & jackpot winner alerts",
                isChecked = userSettings.notificationsEnabled,
                onCheckedChange = {
                    SoundEffectsManager.playButtonClick()
                    viewModel.updateNotificationsEnabled(it)
                },
                testTag = "setting_toggle_notifications"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Haptic Feedback Toggle Item
            SettingToggleItem(
                icon = Icons.Default.PhonelinkRing,
                iconTint = Color(0xFFC084FC),
                title = "Haptic Vibration",
                description = "Tactile vibration feedback on bets & spin wins",
                isChecked = userSettings.hapticFeedbackEnabled,
                onCheckedChange = {
                    SoundEffectsManager.playButtonClick()
                    viewModel.updateHapticFeedbackEnabled(it)
                },
                testTag = "setting_toggle_haptic"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Audio Test Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x1FFFFFFF))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                        .clickable {
                            SoundEffectsManager.playWinSound()
                        }
                        .testTag("test_sound_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎵 Test Win Sound", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x1FFFFFFF))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                        .clickable {
                            SoundEffectsManager.playJackpotSound()
                        }
                        .testTag("test_jackpot_sound_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎰 Test Jackpot Sound", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SettingToggleItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val containerBg by animateColorAsState(
        targetValue = if (isChecked) Color(0x1F10B981) else Color(0x0FFFFFFF),
        animationSpec = tween(300),
        label = "SettingContainerBg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(containerBg)
            .border(
                width = 1.dp,
                color = if (isChecked) iconTint.copy(alpha = 0.4f) else Color(0x1AFFFFFF),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = iconTint,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = Color(0x33FFFFFF)
            )
        )
    }
}

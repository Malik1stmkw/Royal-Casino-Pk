package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "casino_user_settings")

data class UserSettings(
    val musicEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true
)

data class VipLoyaltyState(
    val xp: Int = 1250,
    val tierLevel: Int = 2, // 1: Bronze, 2: Silver, 3: Gold, 4: Platinum, 5: Diamond, 6: Royal Dragon
    val claimedRewardLevel: Int = 1
) {
    val currentTierName: String
        get() = when (tierLevel) {
            1 -> "Bronze VIP"
            2 -> "Silver Crest VIP"
            3 -> "Gold Sovereign VIP"
            4 -> "Platinum Crown VIP"
            5 -> "Diamond Emperor VIP"
            else -> "Royal Dragon VIP"
        }

    val multiplier: Double
        get() = when (tierLevel) {
            1 -> 1.0
            2 -> 1.25
            3 -> 1.50
            4 -> 2.00
            5 -> 2.50
            else -> 3.00
        }

    val iconSymbol: String
        get() = when (tierLevel) {
            1 -> "🥉"
            2 -> "🥈"
            3 -> "🥇"
            4 -> "💎"
            5 -> "👑"
            else -> "🐉"
        }

    val nextTierXpTarget: Int
        get() = when (tierLevel) {
            1 -> 500
            2 -> 1500
            3 -> 3500
            4 -> 7000
            5 -> 15000
            else -> 30000
        }

    val prevTierXp: Int
        get() = when (tierLevel) {
            1 -> 0
            2 -> 500
            3 -> 1500
            4 -> 3500
            5 -> 7000
            else -> 15000
        }

    val progressFraction: Float
        get() {
            val range = (nextTierXpTarget - prevTierXp).coerceAtLeast(1)
            val currentInRange = (xp - prevTierXp).coerceAtLeast(0)
            return (currentInRange.toFloat() / range.toFloat()).coerceIn(0f, 1f)
        }
}

class UserSettingsDataStore(private val context: Context) {

    companion object {
        val KEY_MUSIC = booleanPreferencesKey("music_enabled")
        val KEY_SOUND_EFFECTS = booleanPreferencesKey("sound_effects_enabled")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val KEY_HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback_enabled")

        val KEY_VIP_XP = intPreferencesKey("vip_xp")
        val KEY_VIP_TIER = intPreferencesKey("vip_tier")
        val KEY_VIP_CLAIMED_LEVEL = intPreferencesKey("vip_claimed_level")
    }

    val userSettingsFlow: Flow<UserSettings> = context.settingsDataStore.data.map { preferences ->
        UserSettings(
            musicEnabled = preferences[KEY_MUSIC] ?: true,
            soundEffectsEnabled = preferences[KEY_SOUND_EFFECTS] ?: true,
            notificationsEnabled = preferences[KEY_NOTIFICATIONS] ?: true,
            hapticFeedbackEnabled = preferences[KEY_HAPTIC_FEEDBACK] ?: true
        )
    }

    val vipLoyaltyFlow: Flow<VipLoyaltyState> = context.settingsDataStore.data.map { preferences ->
        val rawXp = preferences[KEY_VIP_XP] ?: 1250
        val computedTier = when {
            rawXp >= 15000 -> 6
            rawXp >= 7000 -> 5
            rawXp >= 3500 -> 4
            rawXp >= 1500 -> 3
            rawXp >= 500 -> 2
            else -> 1
        }
        val storedTier = preferences[KEY_VIP_TIER] ?: computedTier
        val finalTier = Math.max(computedTier, storedTier)
        val claimedLevel = preferences[KEY_VIP_CLAIMED_LEVEL] ?: 1

        VipLoyaltyState(
            xp = rawXp,
            tierLevel = finalTier,
            claimedRewardLevel = claimedLevel
        )
    }

    suspend fun addVipXp(xpGain: Int) {
        context.settingsDataStore.edit { preferences ->
            val currentXp = preferences[KEY_VIP_XP] ?: 1250
            val newXp = currentXp + xpGain
            preferences[KEY_VIP_XP] = newXp

            val newTier = when {
                newXp >= 15000 -> 6
                newXp >= 7000 -> 5
                newXp >= 3500 -> 4
                newXp >= 1500 -> 3
                newXp >= 500 -> 2
                else -> 1
            }
            preferences[KEY_VIP_TIER] = newTier
        }
    }

    suspend fun claimVipLevelReward(tierLevel: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_VIP_CLAIMED_LEVEL] = tierLevel
        }
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_MUSIC] = enabled
        }
    }

    suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_SOUND_EFFECTS] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS] = enabled
        }
    }

    suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_HAPTIC_FEEDBACK] = enabled
        }
    }
}

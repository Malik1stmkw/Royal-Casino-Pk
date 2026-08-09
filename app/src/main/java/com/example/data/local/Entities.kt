package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val username: String = "Zeeshan Graphics",
    val email: String = "zeeshangraphicsmkw@gmail.com",
    val easypaisaNumber: String = "03490802208",
    val jazzcashNumber: String = "03490802208",
    val balance: Double = 10000.0, // Initial Sign-Up bonus Rs. 10,000
    val totalWon: Double = 0.0,
    val totalLost: Double = 0.0,
    val vipLevel: Int = 1,
    val isSignUpBonusClaimed: Boolean = true,
    val lastDailyClaimTime: Long = 0L,
    val dailyStreak: Int = 1,
    val freeSpinsRemaining: Int = 3
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // SIGNUP_BONUS, DAILY_BONUS, DEPOSIT_EASYPAISA, WITHDRAW_EASYPAISA, GAME_WIN, GAME_LOSS, LUCKY_SPIN
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String,
    val accountNumber: String = "",
    val status: String = "SUCCESS" // SUCCESS, PENDING
)

@Entity(tableName = "game_stats")
data class GameStatEntity(
    @PrimaryKey val gameId: Int,
    val gameName: String,
    val timesPlayed: Int = 0,
    val totalWagered: Double = 0.0,
    val totalPayout: Double = 0.0,
    val highestWin: Double = 0.0
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "SYSTEM" // BONUS, MULTIPLAYER, PROMO, TRANSACTION
)

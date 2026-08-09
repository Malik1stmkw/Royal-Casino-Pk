package com.example.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class CasinoRepository(private val dao: CasinoDao) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val transactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val gameStats: Flow<List<GameStatEntity>> = dao.getAllGameStats()
    val notifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val unreadNotificationCount: Flow<Int> = dao.getUnreadNotificationsCount()

    suspend fun ensureProfileInitialized() {
        val existing = dao.getUserProfileSync()
        if (existing == null) {
            val defaultProfile = UserProfileEntity(
                id = 1,
                username = "Zeeshan Graphics",
                email = "zeeshangraphicsmkw@gmail.com",
                easypaisaNumber = "03490802208",
                jazzcashNumber = "03490802208",
                balance = 10000.0, // Sign-up bonus Rs. 10,000
                totalWon = 0.0,
                totalLost = 0.0,
                vipLevel = 1,
                isSignUpBonusClaimed = true,
                lastDailyClaimTime = 0L,
                dailyStreak = 1,
                freeSpinsRemaining = 3
            )
            dao.insertOrUpdateProfile(defaultProfile)

            // Initial Welcome Transaction
            dao.insertTransaction(
                TransactionEntity(
                    type = "SIGNUP_BONUS",
                    amount = 10000.0,
                    note = "Welcome Bonus Credited!",
                    status = "SUCCESS"
                )
            )

            // Initial Push Notifications for Engagement
            dao.insertNotification(
                NotificationEntity(
                    title = "🎉 Welcome to Royal Casino 16-in-1!",
                    message = "Your Rs. 10,000 Welcome Sign-Up Bonus has been credited to your balance!",
                    type = "BONUS"
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    title = "⚡ EasyPaisa & JazzCash Ready",
                    message = "Account 03490802208 linked for instant deposits and cash withdrawals.",
                    type = "TRANSACTION"
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    title = "🔥 16 Live Casino Games Active",
                    message = "Try Slots, Aviator, Dragon vs Tiger, Roulette, Baccarat, Mines and 10 more!",
                    type = "PROMO"
                )
            )
        }
    }

    suspend fun updateBalanceAndLog(
        amountChange: Double,
        type: String,
        note: String,
        accountNumber: String = ""
    ): Boolean {
        val profile = dao.getUserProfileSync() ?: return false
        val newBalance = profile.balance + amountChange
        if (newBalance < 0 && amountChange < 0) {
            return false // Insufficient funds
        }

        val newTotalWon = if (amountChange > 0) profile.totalWon + amountChange else profile.totalWon
        val newTotalLost = if (amountChange < 0) profile.totalLost + (-amountChange) else profile.totalLost

        val updatedProfile = profile.copy(
            balance = newBalance,
            totalWon = newTotalWon,
            totalLost = newTotalLost
        )
        dao.updateProfile(updatedProfile)

        dao.insertTransaction(
            TransactionEntity(
                type = type,
                amount = amountChange,
                note = note,
                accountNumber = accountNumber,
                status = "SUCCESS"
            )
        )
        return true
    }

    suspend fun recordGamePlay(
        gameId: Int,
        gameName: String,
        wagerAmount: Double,
        payoutAmount: Double
    ) {
        val profile = dao.getUserProfileSync() ?: return
        val netChange = payoutAmount - wagerAmount
        val newBalance = (profile.balance + netChange).coerceAtLeast(0.0)

        val updatedProfile = profile.copy(
            balance = newBalance,
            totalWon = if (payoutAmount > 0) profile.totalWon + payoutAmount else profile.totalWon,
            totalLost = if (wagerAmount > payoutAmount) profile.totalLost + (wagerAmount - payoutAmount) else profile.totalLost
        )
        dao.updateProfile(updatedProfile)

        // Log game stat
        val currentStat = dao.getGameStat(gameId)
        val highestWin = Math.max(currentStat?.highestWin ?: 0.0, payoutAmount)
        val updatedStat = GameStatEntity(
            gameId = gameId,
            gameName = gameName,
            timesPlayed = (currentStat?.timesPlayed ?: 0) + 1,
            totalWagered = (currentStat?.totalWagered ?: 0.0) + wagerAmount,
            totalPayout = (currentStat?.totalPayout ?: 0.0) + payoutAmount,
            highestWin = highestWin
        )
        dao.insertOrUpdateGameStat(updatedStat)

        // Log transaction if significant
        if (payoutAmount > wagerAmount * 2) {
            dao.insertTransaction(
                TransactionEntity(
                    type = "GAME_WIN",
                    amount = payoutAmount - wagerAmount,
                    note = "Big Win on $gameName!"
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    title = "🏆 BIG WIN ON $gameName!",
                    message = "Congratulations! You won Rs. ${String.format("%.2f", payoutAmount)} in $gameName!",
                    type = "BONUS"
                )
            )
        }
    }

    suspend fun claimDailyBonus(): Double {
        val profile = dao.getUserProfileSync() ?: return 0.0
        val currentTime = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        val rewardAmount = when (profile.dailyStreak) {
            1 -> 1000.0
            2 -> 2000.0
            3 -> 3500.0
            4 -> 5000.0
            5 -> 7500.0
            6 -> 10000.0
            else -> 15000.0
        }

        val nextStreak = if (profile.dailyStreak >= 7) 1 else profile.dailyStreak + 1
        val updatedProfile = profile.copy(
            balance = profile.balance + rewardAmount,
            lastDailyClaimTime = currentTime,
            dailyStreak = nextStreak,
            freeSpinsRemaining = profile.freeSpinsRemaining + 1
        )
        dao.updateProfile(updatedProfile)

        dao.insertTransaction(
            TransactionEntity(
                type = "DAILY_BONUS",
                amount = rewardAmount,
                note = "Day ${profile.dailyStreak} Login Reward Claimed!"
            )
        )

        dao.insertNotification(
            NotificationEntity(
                title = "🎁 Daily Reward Claimed!",
                message = "Rs. ${String.format("%.0f", rewardAmount)} + 1 Free Spin credited to your wallet!",
                type = "BONUS"
            )
        )

        return rewardAmount
    }

    suspend fun useFreeSpin(): Double {
        val profile = dao.getUserProfileSync() ?: return 0.0
        if (profile.freeSpinsRemaining <= 0) return 0.0

        val spinPrizes = listOf(500.0, 1000.0, 2500.0, 5000.0, 10000.0, 25000.0, 50000.0)
        val prize = spinPrizes.random()

        val updatedProfile = profile.copy(
            balance = profile.balance + prize,
            freeSpinsRemaining = profile.freeSpinsRemaining - 1
        )
        dao.updateProfile(updatedProfile)

        dao.insertTransaction(
            TransactionEntity(
                type = "LUCKY_SPIN",
                amount = prize,
                note = "Lucky Wheel Spin Reward"
            )
        )

        return prize
    }

    suspend fun markNotificationRead(id: Long) {
        dao.markNotificationAsRead(id)
    }
}

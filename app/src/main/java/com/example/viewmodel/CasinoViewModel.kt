package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CasinoRepository
import com.example.data.local.GameStatEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.UserSettings
import com.example.data.local.UserSettingsDataStore
import com.example.data.local.VipLoyaltyState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.delay
import com.example.util.SoundEffectsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Random

data class LiveTickerMessage(
    val playerName: String,
    val gameName: String,
    val amountWon: Double,
    val timestamp: Long = System.currentTimeMillis()
)

class CasinoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CasinoRepository
    val userProfile: StateFlow<UserProfileEntity?>
    val transactions: StateFlow<List<TransactionEntity>>
    val gameStats: StateFlow<List<GameStatEntity>>
    val notifications: StateFlow<List<NotificationEntity>>
    val unreadNotificationsCount: StateFlow<Int>

    private val _liveTicker = MutableStateFlow<List<LiveTickerMessage>>(emptyList())
    val liveTicker: StateFlow<List<LiveTickerMessage>> = _liveTicker.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val settingsDataStore = UserSettingsDataStore(application)
    val userSettings: StateFlow<UserSettings> = settingsDataStore.userSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    val vipLoyaltyState: StateFlow<VipLoyaltyState> = settingsDataStore.vipLoyaltyFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VipLoyaltyState()
    )

    init {
        val dao = AppDatabase.getDatabase(application).casinoDao()
        repository = CasinoRepository(dao)

        viewModelScope.launch {
            userSettings.collectLatest { settings ->
                SoundEffectsManager.setMuted(!settings.soundEffectsEnabled)
            }
        }

        userProfile = repository.userProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        transactions = repository.transactions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        gameStats = repository.gameStats.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        notifications = repository.notifications.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        unreadNotificationsCount = repository.unreadNotificationCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

        viewModelScope.launch {
            repository.ensureProfileInitialized()
            startLiveTickerSimulation()
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun startLiveTickerSimulation() {
        viewModelScope.launch {
            val names = listOf("Ali_Rider", "Usman_PK", "Zeeshan_VIP", "Hamza_786", "Tariq_Pro", "Bilal_King", "Saira_Win", "Rana_Speed", "Kashif_Gold", "Ayesha_Lucky")
            val games = listOf("Slots Jackpot", "Aviator Crash", "Dragon vs Tiger", "Teen Patti 3 Card", "European Roulette", "Mines Sweeper", "Plinko Pyramid", "Coin Flip 3D")
            val random = Random()

            while (true) {
                delay(4000)
                val name = names.random()
                val game = games.random()
                val win = (random.nextInt(40) + 1) * 500.0
                val newMsg = LiveTickerMessage(name, game, win)

                val currentList = _liveTicker.value.toMutableList()
                currentList.add(0, newMsg)
                if (currentList.size > 15) currentList.removeAt(currentList.lastIndex)
                _liveTicker.value = currentList
            }
        }
    }

    fun depositEasyPaisa(amount: Double, accountNum: String, trxId: String) {
        viewModelScope.launch {
            if (amount < 100) {
                _toastMessage.value = "Minimum deposit amount is Rs. 100"
                return@launch
            }
            val success = repository.updateBalanceAndLog(
                amountChange = amount,
                type = "DEPOSIT_EASYPAISA",
                note = "EasyPaisa Deposit (Trx #$trxId)",
                accountNumber = accountNum
            )
            if (success) {
                _toastMessage.value = "Deposit of Rs. ${String.format("%.0f", amount)} via EasyPaisa Successful!"
            } else {
                _toastMessage.value = "Deposit failed. Please check transaction details."
            }
        }
    }

    fun depositJazzCash(amount: Double, accountNum: String, trxId: String) {
        viewModelScope.launch {
            if (amount < 100) {
                _toastMessage.value = "Minimum deposit amount is Rs. 100"
                return@launch
            }
            val success = repository.updateBalanceAndLog(
                amountChange = amount,
                type = "DEPOSIT_JAZZCASH",
                note = "JazzCash Deposit (Trx #$trxId)",
                accountNumber = accountNum
            )
            if (success) {
                _toastMessage.value = "Deposit of Rs. ${String.format("%.0f", amount)} via JazzCash Successful!"
            } else {
                _toastMessage.value = "Deposit failed. Please check transaction details."
            }
        }
    }

    fun withdrawEasyPaisa(amount: Double, accountNum: String) {
        viewModelScope.launch {
            val profile = userProfile.value ?: return@launch
            if (amount < 500) {
                _toastMessage.value = "Minimum withdrawal is Rs. 500"
                return@launch
            }
            if (profile.balance < amount) {
                _toastMessage.value = "Insufficient balance for withdrawal!"
                return@launch
            }

            val success = repository.updateBalanceAndLog(
                amountChange = -amount,
                type = "WITHDRAW_EASYPAISA",
                note = "EasyPaisa Withdrawal to $accountNum",
                accountNumber = accountNum
            )
            if (success) {
                _toastMessage.value = "Withdrawal request of Rs. ${String.format("%.0f", amount)} submitted to $accountNum!"
            } else {
                _toastMessage.value = "Withdrawal failed due to insufficient funds."
            }
        }
    }

    fun withdrawJazzCash(amount: Double, accountNum: String) {
        viewModelScope.launch {
            val profile = userProfile.value ?: return@launch
            if (amount < 500) {
                _toastMessage.value = "Minimum withdrawal is Rs. 500"
                return@launch
            }
            if (profile.balance < amount) {
                _toastMessage.value = "Insufficient balance for withdrawal!"
                return@launch
            }

            val success = repository.updateBalanceAndLog(
                amountChange = -amount,
                type = "WITHDRAW_JAZZCASH",
                note = "JazzCash Withdrawal to $accountNum",
                accountNumber = accountNum
            )
            if (success) {
                _toastMessage.value = "Withdrawal request of Rs. ${String.format("%.0f", amount)} submitted to $accountNum!"
            } else {
                _toastMessage.value = "Withdrawal failed due to insufficient funds."
            }
        }
    }

    fun claimDailyBonus(onSuccess: (Double) -> Unit) {
        viewModelScope.launch {
            val baseReward = repository.claimDailyBonus()
            if (baseReward > 0) {
                val vipMult = vipLoyaltyState.value.multiplier
                val finalBonus = baseReward * vipMult

                if (vipMult > 1.0) {
                    val extraAmount = finalBonus - baseReward
                    repository.updateBalanceAndLog(
                        amountChange = extraAmount,
                        type = "VIP_BONUS_MULTIPLIER",
                        note = "VIP Level ${vipLoyaltyState.value.tierLevel} (${vipLoyaltyState.value.currentTierName}) Multiplier Bonus (+${String.format("%.0f", extraAmount)})"
                    )
                }

                // Award VIP XP on daily bonus claim
                settingsDataStore.addVipXp(200)

                SoundEffectsManager.playJackpotSound()
                _toastMessage.value = "Daily Reward of Rs. ${String.format("%.0f", finalBonus)} (${vipMult}x VIP Multiplier applied) Claimed!"
                onSuccess(finalBonus)
            } else {
                _toastMessage.value = "Daily reward already claimed today!"
            }
        }
    }

    fun spinWheel(onResult: (Double) -> Unit) {
        viewModelScope.launch {
            val prize = repository.useFreeSpin()
            if (prize > 0) {
                // Award VIP XP on wheel spin
                settingsDataStore.addVipXp(100)
                SoundEffectsManager.playWinSound()
                _toastMessage.value = "Lucky Spin Won Rs. ${String.format("%.0f", prize)}! (+100 VIP XP)"
                onResult(prize)
            } else {
                _toastMessage.value = "No free spins remaining! Claim daily bonus for more."
            }
        }
    }

    fun recordGamePlay(gameId: Int, gameName: String, wagerAmount: Double, payoutAmount: Double) {
        viewModelScope.launch {
            repository.recordGamePlay(gameId, gameName, wagerAmount, payoutAmount)

            // Award VIP XP proportional to wager and gameplay
            val xpEarned = (25 + (wagerAmount / 200.0).toInt()).coerceAtMost(500)
            if (xpEarned > 0) {
                settingsDataStore.addVipXp(xpEarned)
            }

            if (payoutAmount >= wagerAmount * 5 && payoutAmount > 0) {
                SoundEffectsManager.playJackpotSound()
            } else if (payoutAmount > 0) {
                SoundEffectsManager.playWinSound()
            } else {
                SoundEffectsManager.playLossSound()
            }
        }
    }

    fun claimVipLevelReward(tierLevel: Int, rewardChips: Double) {
        viewModelScope.launch {
            val success = repository.updateBalanceAndLog(
                amountChange = rewardChips,
                type = "VIP_TIER_UNLOCK",
                note = "VIP Level $tierLevel (${vipLoyaltyState.value.currentTierName}) Unlock Reward Credited!"
            )
            if (success) {
                settingsDataStore.claimVipLevelReward(tierLevel)
                SoundEffectsManager.playJackpotSound()
                _toastMessage.value = "🎉 VIP Level $tierLevel Tier Reward of Rs. ${String.format("%.0f", rewardChips)} Credited to Wallet!"
            }
        }
    }

    fun updateMusicEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setMusicEnabled(enabled)
        }
    }

    fun updateSoundEffectsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setSoundEffectsEnabled(enabled)
            SoundEffectsManager.setMuted(!enabled)
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setNotificationsEnabled(enabled)
        }
    }

    fun updateHapticFeedbackEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setHapticFeedbackEnabled(enabled)
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }
}

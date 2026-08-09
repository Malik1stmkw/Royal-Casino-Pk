package com.example.model

data class GameInfo(
    val id: Int,
    val title: String,
    val category: GameCategory,
    val description: String,
    val minBet: Double = 100.0,
    val maxBet: Double = 100000.0,
    val maxMultiplier: String = "1000x",
    val isHot: Boolean = false,
    val isNew: Boolean = false,
    val livePlayersCount: Int = 124,
    val iconEmoji: String
)

enum class GameCategory(val displayName: String) {
    ALL("All Games"),
    HOT("🔥 Popular"),
    SLOTS("🎰 Slots"),
    CARDS("♠️ Cards"),
    TABLE("🎡 Table"),
    INSTANT("⚡ Instant")
}

object CasinoGamesList {
    val games = listOf(
        GameInfo(
            id = 1,
            title = "Slots Jackpot",
            category = GameCategory.SLOTS,
            description = "3-Reel Classic Fruit Machine with Wilds & Scatter Jackpot!",
            maxMultiplier = "500x",
            isHot = true,
            livePlayersCount = 1420,
            iconEmoji = "🎰"
        ),
        GameInfo(
            id = 2,
            title = "European Roulette",
            category = GameCategory.TABLE,
            description = "Spin the wheel! Place bets on Straight, Colors, Even/Odd & Dozens.",
            maxMultiplier = "36x",
            isHot = true,
            livePlayersCount = 890,
            iconEmoji = "🎡"
        ),
        GameInfo(
            id = 3,
            title = "Blackjack 21",
            category = GameCategory.CARDS,
            description = "Beat the dealer to 21 without going bust. Double down & split pairs!",
            maxMultiplier = "3:2",
            isHot = true,
            livePlayersCount = 670,
            iconEmoji = "♠️"
        ),
        GameInfo(
            id = 4,
            title = "Baccarat Royale",
            category = GameCategory.CARDS,
            description = "Bet on Player, Banker or Tie in high-stakes Baccarat.",
            maxMultiplier = "9x",
            livePlayersCount = 450,
            iconEmoji = "🃏"
        ),
        GameInfo(
            id = 5,
            title = "Coin Flip 3D",
            category = GameCategory.INSTANT,
            description = "Fast Heads or Tails flip! Double your bet instantly in 2 seconds.",
            maxMultiplier = "2x",
            livePlayersCount = 1120,
            iconEmoji = "🪙"
        ),
        GameInfo(
            id = 6,
            title = "Mines Sweeper",
            category = GameCategory.INSTANT,
            description = "Uncover safe tiles without hitting bombs! Cash out anytime.",
            maxMultiplier = "1000x",
            isHot = true,
            isNew = true,
            livePlayersCount = 2150,
            iconEmoji = "💣"
        ),
        GameInfo(
            id = 7,
            title = "Aviator Crash",
            category = GameCategory.INSTANT,
            description = "Watch the rocket fly! Cash out before it crashes in mid-air.",
            maxMultiplier = "5000x",
            isHot = true,
            livePlayersCount = 3890,
            iconEmoji = "🚀"
        ),
        GameInfo(
            id = 8,
            title = "Dragon vs Tiger",
            category = GameCategory.CARDS,
            description = "Asian card duel! Bet Dragon, Tiger or Tie for instant card wins.",
            maxMultiplier = "11x",
            isHot = true,
            livePlayersCount = 1980,
            iconEmoji = "🐉"
        ),
        GameInfo(
            id = 9,
            title = "Teen Patti 3 Card",
            category = GameCategory.CARDS,
            description = "Classic 3-card poker. Trail, Pure Sequence, Pair & High Card!",
            maxMultiplier = "50x",
            isHot = true,
            livePlayersCount = 2450,
            iconEmoji = "🎴"
        ),
        GameInfo(
            id = 10,
            title = "Lucky Wheel Spin",
            category = GameCategory.TABLE,
            description = "Spin the fortune wheel to win multipliers up to 100x & Jackpot!",
            maxMultiplier = "100x",
            livePlayersCount = 830,
            iconEmoji = "🎯"
        ),
        GameInfo(
            id = 11,
            title = "Dice Craps",
            category = GameCategory.TABLE,
            description = "Roll 2 lucky dice! Bet Over 7, Under 7, or Exact Sum.",
            maxMultiplier = "12x",
            livePlayersCount = 320,
            iconEmoji = "🎲"
        ),
        GameInfo(
            id = 12,
            title = "Plinko Pyramid",
            category = GameCategory.INSTANT,
            description = "Drop balls down the peg grid into high payout multiplier slots!",
            maxMultiplier = "100x",
            isNew = true,
            livePlayersCount = 1560,
            iconEmoji = "📊"
        ),
        GameInfo(
            id = 13,
            title = "Scratch Gold Card",
            category = GameCategory.INSTANT,
            description = "Scratch & reveal 3 matching gold symbols to win instant prizes!",
            maxMultiplier = "500x",
            isNew = true,
            livePlayersCount = 940,
            iconEmoji = "🎟️"
        ),
        GameInfo(
            id = 14,
            title = "High-Low Card",
            category = GameCategory.CARDS,
            description = "Guess if the next card is Higher or Lower to build winning streaks!",
            maxMultiplier = "20x",
            livePlayersCount = 510,
            iconEmoji = "📈"
        ),
        GameInfo(
            id = 15,
            title = "Keno Lucky 80",
            category = GameCategory.TABLE,
            description = "Pick 10 numbers from 1 to 80 and match the 20 drawn winning balls!",
            maxMultiplier = "1000x",
            livePlayersCount = 410,
            iconEmoji = "🎱"
        ),
        GameInfo(
            id = 16,
            title = "Video Poker",
            category = GameCategory.CARDS,
            description = "Classic Jacks or Better 5-card draw poker with optimal play strategy.",
            maxMultiplier = "250x",
            livePlayersCount = 630,
            iconEmoji = "📹"
        )
    )
}

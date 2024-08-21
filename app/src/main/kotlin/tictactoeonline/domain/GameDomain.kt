package tictactoeonline.domain

import tictactoeonline.util.MyStringTools

interface Game {
    fun initializeField(size: String)

    companion object {
        fun privateRoomToken(): String = MyStringTools.randomStringByKotlinRandom(32)
    }
}

enum class GameState(val description: String) {
    NOT_STARTED("game not started"),
    PLAYER_MOVE_1("1st player's move"),
    PLAYER_MOVE_2("2nd player's move"),
    GAME_OVER_WINNER_1("1st player won"),
    GAME_OVER_WINNER_2("2nd player won"),
    GAME_OVER_DRAW("draw"),
}
package tictactoeonline.domain

import tictactoeonline.User

class Player(val user: User, val marker: Char = '?') {
    val name: String = user.email
    val locations = mutableSetOf<CellLocation>()

    override fun toString(): String {
        return "Player: user=$user, name=$name, marker=$marker, no. locations: ${locations.size}"
    }

}


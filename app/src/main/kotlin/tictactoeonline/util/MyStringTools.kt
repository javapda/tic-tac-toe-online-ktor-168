package tictactoeonline.util

import kotlin.random.Random

class MyStringTools {
    companion object {
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        /**
         * Random string by kotlin random
         * credit: https://www.baeldung.com/kotlin/random-alphanumeric-string
         * @param stringLength
         * @return
         */
        fun randomStringByKotlinRandom(stringLength: Int = 5): String =
            (1..stringLength)
                .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
                .joinToString("")
    }
}
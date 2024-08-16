package tictactoeonline.hyperskill

class CheckResult(private val isCorrect: Boolean, private val feedback: String = "") {

    fun isCorrect(): Boolean = this.isCorrect
    fun getFeedback(): String = this.feedback

    override fun toString(): String = "isCorrect=$isCorrect, feedback=$feedback"

    companion object {
        fun correct() = CheckResult(true, "")
        fun wrong(feedback: String) = CheckResult(false, feedback)
    }
}
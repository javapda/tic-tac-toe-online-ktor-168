package tictactoeonline

class WrongAnswer(private val feedbackText: String) : OutcomeError(feedbackText) {
    fun getFeedbackText(): String = feedbackText
}
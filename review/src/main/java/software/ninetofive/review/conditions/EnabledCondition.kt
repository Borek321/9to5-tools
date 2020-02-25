package software.ninetofive.review.conditions

class EnabledCondition(private val isEnabled: Boolean): AskForReviewCondition {

    override fun hasConditionBeenMade(): Boolean {
        return isEnabled
    }

}
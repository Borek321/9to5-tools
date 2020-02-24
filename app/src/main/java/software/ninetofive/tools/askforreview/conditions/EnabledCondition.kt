package software.ninetofive.tools.askforreview.conditions

class EnabledCondition(private val isEnabled: Boolean): AskForReviewCondition {

    override fun hasConditionBeenMade(): Boolean {
        return isEnabled
    }

}
package software.ninetofive.review.conditions

import android.content.Context
import software.ninetofive.review.util.AskForReviewSharedPreferences

class AlreadyShowedCondition(
    context: Context,
    private val sharedPreferences: AskForReviewSharedPreferences = AskForReviewSharedPreferences(context)
): AskForReviewCondition {

    override fun hasConditionBeenMade(): Boolean {
        return sharedPreferences.getAlreadyShowed()
    }

}
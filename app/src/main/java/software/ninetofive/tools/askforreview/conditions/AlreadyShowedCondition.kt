package software.ninetofive.tools.askforreview.conditions

import android.content.Context
import software.ninetofive.tools.askforreview.util.AskForReviewSharedPreferences

class AlreadyShowedCondition(context: Context): AskForReviewCondition {

    private val sharedPreferences: AskForReviewSharedPreferences = AskForReviewSharedPreferences(context)

    override fun hasConditionBeenMade(): Boolean {
        return sharedPreferences.getAlreadyShowed()
    }

}
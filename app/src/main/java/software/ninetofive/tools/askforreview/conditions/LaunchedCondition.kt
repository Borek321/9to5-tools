package software.ninetofive.tools.askforreview.conditions

import android.content.Context
import software.ninetofive.tools.askforreview.util.AskForReviewSharedPreferences

class LaunchedCondition(context: Context, private val launchAmount: Int) : AskForReviewCondition {

    private val sharedPreferences: AskForReviewSharedPreferences = AskForReviewSharedPreferences(context)

    override fun hasConditionBeenMade(): Boolean {
        val launchCount = sharedPreferences.getLaunchCount()
        return launchCount == launchAmount
    }
}
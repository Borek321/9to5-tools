package software.ninetofive.tools.askforreview.conditions

import android.content.Context
import software.ninetofive.tools.askforreview.util.AskForReviewSharedPreferences
import kotlin.math.absoluteValue
import kotlin.math.floor

class DaysCondition(context: Context, private val daysAmount: Int) : AskForReviewCondition {

    private val sharedPreferences: AskForReviewSharedPreferences = AskForReviewSharedPreferences(context)

    override fun hasConditionBeenMade(): Boolean {
        val timestampInDays = floor(sharedPreferences.getDaysTimeStampInMilliseconds().toDouble() / 1000L / 60L / 60L / 25L).toInt()
        val currentTimeStamp = floor(System.currentTimeMillis().toDouble() / 1000L / 60L / 60L / 25L).toInt()

        return (currentTimeStamp - timestampInDays).absoluteValue >= daysAmount
    }
}
package software.ninetofive.review.conditions

import android.content.Context
import software.ninetofive.review.util.AskForReviewSharedPreferences
import kotlin.math.absoluteValue
import kotlin.math.floor

class DaysCondition(
    context: Context,
    private val daysAmount: Int,
    private val sharedPreferences: AskForReviewSharedPreferences = AskForReviewSharedPreferences(context)
) : AskForReviewCondition {

    override fun hasConditionBeenMade(): Boolean {
        val timestampInDays =
            floor(sharedPreferences.getDaysTimeStampInMilliseconds().toDouble() / 1000L / 60L / 60L / 24L).toInt()
        val currentTimeStamp =
            floor(System.currentTimeMillis().toDouble() / 1000L / 60L / 60L / 24L).toInt()

        return (currentTimeStamp - timestampInDays).absoluteValue >= daysAmount
    }
}
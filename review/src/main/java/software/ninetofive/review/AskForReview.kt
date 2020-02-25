package software.ninetofive.review

import android.app.Dialog
import android.content.Context
import software.ninetofive.review.conditions.AskForReviewCondition
import software.ninetofive.review.util.AskForReviewSharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AskForReview @Inject constructor() {

    private lateinit var preferences: AskForReviewSharedPreferences
    private var conditions: List<AskForReviewCondition> = listOf()

    fun initialize(context: Context, conditions: List<AskForReviewCondition>) {
        initialize(conditions, AskForReviewSharedPreferences(context))
    }

    fun initialize(conditions: List<AskForReviewCondition>, preferences: AskForReviewSharedPreferences) {
        this.conditions = conditions
        preferences.incrementLaunchCount()
        preferences.setDays(System.currentTimeMillis())
    }

    fun canShowDialog(): Boolean {
        return this.conditions.all { it.hasConditionBeenMade() }
    }

    fun showDialog(dialog: AskForReviewDialog, showDialog: (Dialog) -> Unit) {
        if (canShowDialog()) {
            showDialog(dialog.create())
            preferences.setAlreadyShowed()
        }
    }

    companion object {
        val instance: AskForReview = AskForReview()
    }

}
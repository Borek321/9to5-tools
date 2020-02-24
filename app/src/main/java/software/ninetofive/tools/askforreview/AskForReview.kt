package software.ninetofive.tools.askforreview

import android.app.Dialog
import android.content.Context
import software.ninetofive.tools.askforreview.conditions.AskForReviewCondition
import software.ninetofive.tools.askforreview.util.AskForReviewSharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AskForReview @Inject constructor() {

    private lateinit var preferences: AskForReviewSharedPreferences
    private var conditions: List<AskForReviewCondition> = listOf()

    fun initialize(context: Context, conditions: List<AskForReviewCondition>) {
        this.conditions = conditions

        preferences = AskForReviewSharedPreferences(context)
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
package software.ninetofive.review

import android.app.Dialog
import android.content.Context
import android.util.Log
import software.ninetofive.review.conditions.AskForReviewCondition
import software.ninetofive.review.util.AskForReviewSharedPreferences
import software.ninetofive.review.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AskForReview @Inject constructor() {

    internal var logger: Logger = Logger()

    private var initialized: Boolean = false
    private lateinit var preferences: AskForReviewSharedPreferences
    private var conditions: List<AskForReviewCondition> = listOf()

    fun initialize(context: Context, conditions: List<AskForReviewCondition>) {
        initialize(conditions, AskForReviewSharedPreferences(context))
    }

    fun canShowDialog(): Boolean {
        if (!initialized) {
            logger.logError("Ask For Review has not yet been initialized!")
        }
        return initialized && this.conditions.all { it.hasConditionBeenMade() }
    }

    fun showDialog(dialog: AskForReviewDialog, showDialog: (Dialog) -> Unit) {
        if (!initialized) {
            logger.logError("Ask For Review has not yet been initialized!")
        }

        if (initialized && canShowDialog()) {
            showDialog(dialog.create())
            preferences.setAlreadyShowed()
        }
    }

    internal fun initialize(conditions: List<AskForReviewCondition>, preferences: AskForReviewSharedPreferences) {
        this.conditions = conditions
        this.preferences = preferences
        this.initialized = true

        preferences.incrementLaunchCount()
        preferences.setDays(System.currentTimeMillis())
    }

    companion object {
        val instance: AskForReview = AskForReview()
    }

}
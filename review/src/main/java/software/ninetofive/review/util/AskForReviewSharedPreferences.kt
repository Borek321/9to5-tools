package software.ninetofive.review.util

import android.content.Context
import android.content.SharedPreferences

open class AskForReviewSharedPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    private val currentVersion: String = context.packageManager.getPackageInfo(context.packageName, 0).versionName

    private val launchedKey: String = "${SHARED_PREFERENCES_KEY_LAUNCHED}-$currentVersion"
    private val daysKey: String = "${SHARED_PREFERENCES_KEY_DAYS}-$currentVersion"
    private val alreadyShowedKey: String = "${SHARED_PREFERENCES_KEY_ALREADY_SHOWED}-$currentVersion"

    open fun getLaunchCount(): Int {
        return sharedPreferences.getInt(launchedKey, 0)
    }

    open fun getDaysTimeStampInMilliseconds(): Long {
        return sharedPreferences.getLong(daysKey, -1L)
    }

    open fun getAlreadyShowed(): Boolean {
        return sharedPreferences.getBoolean(alreadyShowedKey, false)
    }

    open fun incrementLaunchCount() {
        sharedPreferences.edit().putInt(launchedKey, getLaunchCount() + 1).apply()
    }

    open fun setDays(epochInMilliseconds: Long) {
        val hasDaySetForThisVersion = getDaysTimeStampInMilliseconds() >= 0L
        if (!hasDaySetForThisVersion) {
            sharedPreferences.edit().putLong(daysKey, epochInMilliseconds).apply()
        }
    }

    open fun setAlreadyShowed() {
        sharedPreferences.edit().putBoolean(alreadyShowedKey, true).apply()
    }

    companion object {
        private const val SHARED_PREFERENCES_KEY = "software.ninetofive.tools.askforreview.shared_preferences"
        private const val SHARED_PREFERENCES_KEY_LAUNCHED = "software.ninetofive.tools.askforreview.shared_preferences.launched"
        private const val SHARED_PREFERENCES_KEY_DAYS = "software.ninetofive.tools.askforreview.shared_preferences.days"
        private const val SHARED_PREFERENCES_KEY_ALREADY_SHOWED = "software.ninetofive.tools.askforreview.shared_preferences.already_showed"
    }

}
package software.ninetofive.tools

import android.content.Context
import android.content.SharedPreferences
import org.threeten.bp.LocalDate
import software.ninetofive.tools.factory.DateFactory
import software.ninetofive.tools.factory.DialogFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class RateAppHelper @Inject constructor(
    private val dateFactory: DateFactory,
    private val dialogFactory: DialogFactory
) {

    // Dialog

    fun showRatingDialogIfNeeded(context: Context, onClickRate: (Boolean) -> Unit) {
        if (shouldShowDialog(context)) {
            showDialog(context, onClickRate)
            saveDialogShown(context)
        }
    }

    fun shouldShowDialog(context: Context): Boolean {
//        val isRatingEnabled = remoteConfig.requestReview.value ?: false
//
//        var askAfterTimesOpenedApp =
//            remoteConfig.reviewMinLaunchesUntilPrompt.value ?: 0
//        if (askAfterTimesOpenedApp == 0) {
//            askAfterTimesOpenedApp = FALL_BACK_VALUE_LAUNCH
//        }
//        var askAfterDays = remoteConfig.reviewMinDaysUntilPrompt.value ?: 0
//        if (askAfterDays == 0) {
//            askAfterDays = FALL_BACK_VALUE_DAYS
//        }
//
//        return shouldShowDialog(context, isRatingEnabled, askAfterTimesOpenedApp, askAfterDays)
        return true
    }

    fun shouldShowDialog(
        context: Context,
        isRatingEnabled: Boolean,
        askAfterTimesOpenedApp: Int,
        askAfterDays: Int
    ): Boolean {
        val launchPreferences = launchPreferences(context)
        val dayPreferences = daysSharedPreferences(context)
        val dialogShownPreferences = dialogShownPreferences(context)

        val currentVersion = currentVersion(context)

        val launchedTime = launchPreferences.getInt(createLaunchedVersionKey(currentVersion), 0)
        val epochDay = dayPreferences.getLong(createLaunchedDaysVersionKey(currentVersion), 0)
        val difference =
            dateFactory.periodBetween(dateFactory.fromEpochDay(epochDay), dateFactory.now()).days

        val hasLaunchedEnough = launchedTime >= askAfterTimesOpenedApp
        val hasAppForDays = difference >= askAfterDays

        val alreadyShownDialog =
            dialogShownPreferences.getBoolean(createShownDialogVersionKey(currentVersion), false)

        return isRatingEnabled && (hasLaunchedEnough || hasAppForDays) && !alreadyShownDialog
    }

    private fun showDialog(context: Context, onClickRate: (Boolean) -> Unit) {
        dialogFactory.createRateAppDialog(context, onClickRate).show()
    }

    // SharedPreferences updates

    fun incrementLaunchCount(context: Context) {
        val currentVersion = currentVersion(context)
        val key = createLaunchedVersionKey(currentVersion)
        val launchCount = launchPreferences(context).getInt(key, 0) + 1
        val editor = launchPreferences(context).edit()
        editor.clear().apply()
        editor.putInt(key, launchCount).apply()
    }

    fun saveNewDaysTimeStamp(timestamp: LocalDate, context: Context) {
        val currentVersion = currentVersion(context)
        val key = createLaunchedDaysVersionKey(currentVersion)
        val preferences = daysSharedPreferences(context)
        val hasAnTimestampForThisVersion = preferences.getLong(key, -1L)
        val editor = daysSharedPreferences(context).edit()
        if (hasAnTimestampForThisVersion == -1L) {
            editor.clear().apply()
            editor.putLong(key, timestamp.toEpochDay()).apply()
        }
    }

    private fun saveDialogShown(context: Context) {
        val currentVersion = currentVersion(context)
        val key = createShownDialogVersionKey(currentVersion)
        val preferences = dialogShownPreferences(context)
        val isShown = preferences.getBoolean(key, false)
        if (!isShown) {
            preferences.edit().clear().apply()
            preferences.edit().putBoolean(key, true).apply()
        }
    }

    // SharedPreferences

    open fun launchPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(RATE_APP_PREFERENCES, Context.MODE_PRIVATE)

    private fun daysSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(RATE_APP_DAYS_PREFERENCES, Context.MODE_PRIVATE)

    private fun dialogShownPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(RATE_APP_DIALOG_SHOWN_PREFERENCES, Context.MODE_PRIVATE)

    // Key creation

    fun createLaunchedVersionKey(versionNumber: String): String =
        "$RATE_APP_PREFERENCES_KEY_LAUNCHED-$versionNumber-launch-count"

    private fun createLaunchedDaysVersionKey(versionNumber: String): String =
        "$RATE_APP_PREFERENCES_KEY_LAUNCHED_DAYS-$versionNumber-days-count"

    private fun createShownDialogVersionKey(versionNumber: String): String =
        "$RATE_APP_PREFERENCES_KEY_SHOWN_DIALOG-$versionNumber-dialog-shown"

    // Private functions

    open fun currentVersion(context: Context): String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName

    companion object {

        const val RATE_APP_PREFERENCES = "nl.ninetofive.nummi.util.RATE_APP_PREFERENCES"
        const val RATE_APP_DAYS_PREFERENCES =
            "nl.ninetofive.nummi.util.RATE_APP_DAYS_PREFERENCES"
        const val RATE_APP_DIALOG_SHOWN_PREFERENCES =
            "nl.ninetofive.nummi.util.RATE_APP_DIALOG_SHOWN_PREFERENCES"

        private const val RATE_APP_PREFERENCES_KEY_LAUNCHED = "RateThisAppPreferencesKeyLaunched"
        private const val RATE_APP_PREFERENCES_KEY_LAUNCHED_DAYS =
            "RateThisAppPreferencesKeyLaunchedDays"
        private const val RATE_APP_PREFERENCES_KEY_SHOWN_DIALOG =
            "RateThisAppPreferencesKeyLaunchedShownDialog"

        private const val FALL_BACK_VALUE_DAYS = 7
        private const val FALL_BACK_VALUE_LAUNCH = 7
    }
}
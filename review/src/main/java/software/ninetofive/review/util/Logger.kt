package software.ninetofive.review.util

import android.util.Log

open class Logger {

    open fun logError(message: String) {
        Log.e("AFR", message)
    }

}
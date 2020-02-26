package software.ninetofive.photoselector.util

import android.os.Build
import javax.inject.Inject

open class BuildVersionUtil @Inject constructor() {

    open fun getAndroidApiVersion(): Int {
        return Build.VERSION.SDK_INT
    }

}
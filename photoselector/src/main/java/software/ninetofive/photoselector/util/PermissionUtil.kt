package software.ninetofive.photoselector.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import javax.inject.Inject

open class PermissionUtil @Inject constructor(
    private val buildVersionUtil: BuildVersionUtil
) {

    var isRationaleShown: Boolean = false
    var showRationale: (() -> Unit)? = null

    @SuppressLint("NewApi")
    open fun requestCameraPermissions(activity: Activity? = null, fragment: Fragment? = null) {
        val showPermissionRationale = !isRationaleShown && shouldShowRationale(activity, fragment)
        requestCameraPermissions(activity, fragment, showPermissionRationale)
    }

    open fun hasCameraPermission(context: Context): Boolean {
        return hasPermission(context, Manifest.permission.CAMERA)
    }

    fun isPermissionGranted(requestCode: Int, grantResults: IntArray): Boolean {
        return requestCode == REQUEST_CAMERA_PERMISSION_CODE
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("NewApi")
    open fun shouldShowRationale(activity: Activity? = null, fragment: Fragment? = null): Boolean {
        return if (buildVersionUtil.getAndroidApiVersion() >= 23) {
            activity?.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                ?: fragment?.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                ?: false
        } else {
            false
        }
    }

    // Private functions

    private fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("NewApi")
    private fun requestCameraPermissions(activity: Activity? = null, fragment: Fragment? = null, shouldShowPermissionRationale: Boolean) {
        if (shouldShowPermissionRationale) {
            showRationale?.invoke()
        } else if (buildVersionUtil.getAndroidApiVersion() >= 23) {
            activity?.requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION_CODE)
            fragment?.requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION_CODE)
        }
    }

    companion object {
        const val REQUEST_CAMERA_PERMISSION_CODE = 1522
    }

}
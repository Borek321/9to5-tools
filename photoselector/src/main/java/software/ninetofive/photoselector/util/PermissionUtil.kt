package software.ninetofive.photoselector.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import javax.inject.Inject

class PermissionUtil @Inject constructor() {

    var isRationaleShown: Boolean = false
    var showRationale: (() -> Unit)? = null

    fun requestCameraPermissions(activity: Activity) {
        if (!hasCameraPermission(activity) && Build.VERSION.SDK_INT >= 23) {
            val showPermissionRationale = !isRationaleShown && activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            requestCameraPermissions(activity, showPermissionRationale)
        }
    }

    fun requestCameraPermissions(fragment: Fragment) {
        val context = fragment.context ?: return

        if (!hasCameraPermission(context) && Build.VERSION.SDK_INT >= 23) {
            val showPermissionRationale = !isRationaleShown && fragment.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            requestCameraPermissions(fragment, showPermissionRationale)
        }
    }

    fun hasCameraPermission(context: Context): Boolean {
        return hasPermission(context, Manifest.permission.CAMERA)
    }

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun isPermissionGranted(requestCode: Int, grantResults: IntArray): Boolean {
        return requestCode == REQUEST_CAMERA_PERMISSION_CODE
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    // Private functions

    @RequiresApi(23)
    private fun requestCameraPermissions(activity: Activity, shouldShowPermissionRationale: Boolean) {
        if (shouldShowPermissionRationale) {
            showRationale?.invoke()
        } else {
            activity.requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION_CODE)
        }
    }

    @RequiresApi(23)
    private fun requestCameraPermissions(fragment: Fragment, shouldShowPermissionRationale: Boolean) {
        if (shouldShowPermissionRationale) {

        } else {
            fragment.requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION_CODE)
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION_CODE = 1522
    }

}
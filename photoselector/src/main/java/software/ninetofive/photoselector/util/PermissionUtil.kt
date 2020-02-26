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

open class PermissionUtil @Inject constructor() {

    var isRationaleShown: Boolean = false
    var showRationale: (() -> Unit)? = null

    open fun requestCameraPermissions(activity: Activity? = null, fragment: Fragment? = null) {
        val context = activity ?: fragment?.context ?: return

        if (!hasCameraPermission(context) && Build.VERSION.SDK_INT >= 23) {
            val showPermissionRationale = !isRationaleShown && shouldShowRationale(activity, fragment)
            requestCameraPermissions(activity, fragment, showPermissionRationale)
        }
    }

    open fun hasCameraPermission(context: Context): Boolean {
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

    open fun shouldShowRationale(activity: Activity? = null, fragment: Fragment? = null): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                ?: fragment?.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                ?: false
        } else {
            false
        }
    }

    // Private functions

    @RequiresApi(23)
    private fun requestCameraPermissions(activity: Activity? = null, fragment: Fragment? = null, shouldShowPermissionRationale: Boolean) {
        if (shouldShowPermissionRationale) {
            showRationale?.invoke()
        } else {
            activity?.requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION_CODE)
            fragment?.requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION_CODE)
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION_CODE = 1522
    }

}
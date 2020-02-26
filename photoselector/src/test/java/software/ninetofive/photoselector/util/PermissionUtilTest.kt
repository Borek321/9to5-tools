package software.ninetofive.photoselector.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class PermissionUtilTest {

    lateinit var buildConfUtil: BuildVersionUtil

    lateinit var util: PermissionUtil

    @Before
    fun setUp() {
        buildConfUtil = mock {
            on { this.getAndroidApiVersion() } doReturn 24
        }
        util = PermissionUtil(buildConfUtil)
    }

    @Test
    fun requestCameraPermissions_invokesShowRationaleIfShould() {
        val activity: Activity = mock()
        val showRationale: (() -> Unit)? = mock()
        util.showRationale = showRationale
        util.isRationaleShown = false
        Mockito.`when`(activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
            .doReturn(true)

        util.requestCameraPermissions(activity)

        verify(showRationale)?.invoke()
    }

    @Test
    fun requestCameraPermissions_requestsPermissionsIfRationalAlradyShown() {
        val activity: Activity = mock()
        val showRationale: (() -> Unit)? = mock()
        util.showRationale = showRationale
        util.isRationaleShown = true
        Mockito.`when`(activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
            .doReturn(true)

        util.requestCameraPermissions(activity)

        verify(activity).requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            PermissionUtil.REQUEST_CAMERA_PERMISSION_CODE
        )
    }

    @Test
    fun requestCameraPermissions_requestsPermissions() {
        val activity: Activity = mock()
        Mockito.`when`(activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
            .doReturn(false)

        util.requestCameraPermissions(activity)

        verify(activity).requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            PermissionUtil.REQUEST_CAMERA_PERMISSION_CODE
        )
    }

    @Test
    fun isPermissionGranted_returnsTrueIfValid() {
        assertTrue(
            util.isPermissionGranted(
                PermissionUtil.REQUEST_CAMERA_PERMISSION_CODE,
                intArrayOf(PackageManager.PERMISSION_GRANTED)
            )
        )
        assertFalse(
            util.isPermissionGranted(
                PermissionUtil.REQUEST_CAMERA_PERMISSION_CODE - 1,
                intArrayOf(PackageManager.PERMISSION_GRANTED)
            )
        )
        assertFalse(
            util.isPermissionGranted(
                PermissionUtil.REQUEST_CAMERA_PERMISSION_CODE,
                intArrayOf()
            )
        )
        assertFalse(
            util.isPermissionGranted(
                PermissionUtil.REQUEST_CAMERA_PERMISSION_CODE,
                intArrayOf(PackageManager.PERMISSION_DENIED)
            )
        )
        assertFalse(
            util.isPermissionGranted(
                PermissionUtil.REQUEST_CAMERA_PERMISSION_CODE,
                intArrayOf(PackageManager.PERMISSION_GRANTED - 1)
            )
        )
    }

    @Test
    fun shouldShowRationale_returnsFalsIfOldBuildVersion() {
        Mockito.`when`(buildConfUtil.getAndroidApiVersion()).doReturn(10)
        assertFalse(util.shouldShowRationale(mock()))
    }

    @Test
    fun shouldShowRationale_returnsTrueIfActivityShouldShowReturnsTrue() {
        val activity: Activity =
            mock { on { this.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) } doReturn true }
        assertTrue(util.shouldShowRationale(activity))

        val fragment: Fragment =
            mock { on { this.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) } doReturn true }
        assertTrue(util.shouldShowRationale(fragment = fragment))
    }

    @Test
    fun shouldShowRationale_returnsFalseIfEveryParameterIsNull() {
        assertFalse(util.shouldShowRationale())
    }
}
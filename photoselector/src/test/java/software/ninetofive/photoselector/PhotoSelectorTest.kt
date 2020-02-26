package software.ninetofive.photoselector

import android.app.Activity
import android.app.Dialog
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import software.ninetofive.photoselector.factory.DialogFactory
import software.ninetofive.photoselector.factory.FileUriFactory
import software.ninetofive.photoselector.factory.IntentFactory
import software.ninetofive.photoselector.interfaces.PhotoSelectorListener
import software.ninetofive.photoselector.util.FileUtil
import software.ninetofive.photoselector.util.PermissionUtil
import java.io.File

class PhotoSelectorTest {

    lateinit var dialogFactory: DialogFactory
    lateinit var permissionUtil: PermissionUtil
    lateinit var intentFactory: IntentFactory
    lateinit var fileUtil: FileUtil
    lateinit var fileUriFactory: FileUriFactory

    lateinit var photoSelector: PhotoSelector

    @Before
    fun setUp() {
        dialogFactory = mock()
        permissionUtil = mock()
        intentFactory = mock()
        fileUriFactory = mock()
        fileUtil = mock()

        photoSelector = PhotoSelector(dialogFactory, permissionUtil, intentFactory, fileUtil, fileUriFactory)
    }

    @Test
    fun start_doesNothingWhenAuthorityNotProvidedAreNotValidAndCallsListener() {
        val options: Map<PhotoSelector.Options, Any> = mapOf()
        val activity: Activity = mock()

        photoSelector.start(activity = activity, options = options, listener = object : PhotoSelectorListener {
            override fun onSuccessPhotoSelected(imageFile: File) {
                fail()
            }

            override fun onFailurePhotoSelected(exception: PhotoSelectorException) {
                assertEquals(RequiredOptionException(PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME).message, exception.message)
            }
        })

        verifyZeroInteractions(dialogFactory)
    }

    @Test
    fun start_doesNothingWhenHandlerNotProvidedButRequireddAndCallsListener() {
        val options: Map<PhotoSelector.Options, Any> = mapOf(PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME to "asdf")
        val activity: Activity = mock()
        Mockito.`when`(permissionUtil.shouldShowRationale(activity, null)).thenReturn(true)

        photoSelector.start(activity = activity, options = options, listener = object : PhotoSelectorListener {
            override fun onSuccessPhotoSelected(imageFile: File) {
                fail()
            }

            override fun onFailurePhotoSelected(exception: PhotoSelectorException) {
                assertEquals(RequiredOptionException(PhotoSelector.Options.RATIONALE_HANDLER).message, exception.message)
            }
        })

        verifyZeroInteractions(dialogFactory)
    }

    @Test
    fun start_showsDialog() {
        val options: Map<PhotoSelector.Options, Any> = mapOf(PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME to "asdf")
        val activity: Activity = mock()
        val dialog: Dialog = mock()
        doNothing().`when`(dialog).show()
        Mockito.`when`(permissionUtil.shouldShowRationale(eq(activity), any())).thenReturn(false)
        Mockito.`when`(dialogFactory.createTypeSelectionDialog(eq(activity), any(), any(), any(), any(), any())).then {
            dialog
        }

        photoSelector.start(activity = activity, options = options, listener = mock())

        verify(dialog).show()
    }

}
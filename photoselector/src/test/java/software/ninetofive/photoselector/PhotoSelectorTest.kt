package software.ninetofive.photoselector

import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
import software.ninetofive.photoselector.util.BitmapUtil
import software.ninetofive.photoselector.util.FileUtil
import software.ninetofive.photoselector.util.MimeUtil
import software.ninetofive.photoselector.util.PermissionUtil
import java.io.File

class PhotoSelectorTest {

    lateinit var dialogFactory: DialogFactory
    lateinit var permissionUtil: PermissionUtil
    lateinit var intentFactory: IntentFactory
    lateinit var fileUtil: FileUtil
    lateinit var fileUriFactory: FileUriFactory
    lateinit var mimeUtil: MimeUtil
    lateinit var bitmapUtil: BitmapUtil

    lateinit var photoSelector: PhotoSelector

    @Before
    fun setUp() {
        dialogFactory = mock()
        permissionUtil = mock()
        intentFactory = mock()
        fileUriFactory = mock()
        fileUtil = mock()
        mimeUtil = mock()
        bitmapUtil = mock()

        photoSelector = PhotoSelector(dialogFactory, permissionUtil, intentFactory, fileUtil, fileUriFactory, mimeUtil, bitmapUtil)
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

    @Test
    fun startTakePicture_doesNothingWhenAuthorityNotProvidedAreNotValidAndCallsListener() {
        val options: Map<PhotoSelector.Options, Any> = mapOf()
        val activity: Activity = mock()

        photoSelector.startTakePicture(activity = activity, options = options, listener = object : PhotoSelectorListener {
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
    fun startTakePicture_doesNothingWhenHandlerNotProvidedButRequireddAndCallsListener() {
        val options: Map<PhotoSelector.Options, Any> = mapOf(PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME to "asdf")
        val activity: Activity = mock()
        Mockito.`when`(permissionUtil.shouldShowRationale(activity, null)).thenReturn(true)

        photoSelector.startTakePicture(activity = activity, options = options, listener = object : PhotoSelectorListener {
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
    fun startTakePicture_requestsPermissionIfNoPermissions() {
        val options: Map<PhotoSelector.Options, Any> = mapOf(PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME to "asdf")
        val activity: Activity = mock()
        Mockito.`when`(permissionUtil.shouldShowRationale(eq(activity), any())).thenReturn(false)
        Mockito.`when`(permissionUtil.hasCameraPermission(activity)).doReturn(false)

        photoSelector.startTakePicture(activity = activity, options = options, listener = mock())
        verify(permissionUtil).requestCameraPermissions(activity, null)
    }

    @Test
    fun startTakePicture_createsImageAndStartsActivity() {
        val options: Map<PhotoSelector.Options, Any> = mapOf(PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME to "asdf")
        val activity: Activity = mock()
        val imageFile: File = mock()
        val intent: Intent = mock()
        Mockito.`when`(permissionUtil.shouldShowRationale(eq(activity), any())).thenReturn(false)
        Mockito.`when`(permissionUtil.hasCameraPermission(activity)).doReturn(true)
        Mockito.`when`(fileUtil.createJpegImageFile(activity, "jpeg")).thenReturn(imageFile)
        Mockito.`when`(intentFactory.createTakePictureIntent(activity)).thenReturn(intent)

        photoSelector.startTakePicture(activity = activity, options = options, listener = mock())

        verify(intentFactory).createTakePictureIntent(activity)
        verify(fileUriFactory).createUriForFile(imageFile, "asdf", activity)
        verify(activity).startActivityForResult(eq(intent), any())
    }

    @Test
    fun startSelectImage_startsActivity() {
        val activity: Activity = mock()
        val intent: Intent = mock()
        Mockito.`when`(intentFactory.createSelectImageIntent(activity)).doReturn(intent)

        photoSelector.startSelectImage(activity, options = mapOf(), listener = mock())

        verify(activity).startActivityForResult(eq(intent), any())
    }

    @Test
    fun onActivityResult_callsOnFailureIfSomethingWentWrong() {
        val activity: Activity = mock()
        val intent: Intent = mock()
        Mockito.`when`(intentFactory.createSelectImageIntent(activity)).doReturn(intent)

        photoSelector.startSelectImage(activity, options = mapOf(), listener = object : PhotoSelectorListener {
            override fun onSuccessPhotoSelected(imageFile: File) {
                fail()
            }

            override fun onFailurePhotoSelected(exception: PhotoSelectorException) {
                assertEquals(GenericException().message, exception.message)
            }

        })
        photoSelector.onActivityResult(24, Activity.RESULT_OK - 1, mock())
    }

    @Test
    fun onActivityResult_TakePicture_callsOnFailureIfNoFile() {
        val activity: Activity = mock()
        val intent: Intent = mock()
        Mockito.`when`(intentFactory.createSelectImageIntent(activity)).doReturn(intent)

        photoSelector.startSelectImage(activity, options = mapOf(), listener = object : PhotoSelectorListener {
            override fun onSuccessPhotoSelected(imageFile: File) {
                fail()
            }

            override fun onFailurePhotoSelected(exception: PhotoSelectorException) {
                assertEquals(GenericException().message, exception.message)
            }

        })
        photoSelector.onActivityResult(PhotoSelector.TAKE_PICTURE_REQUEST_CODE, Activity.RESULT_OK, mock())
    }

    @Test
    fun onActivityResult_TakePicture_callsOnSuccessIfFile() {
        val options: Map<PhotoSelector.Options, Any> = mapOf(PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME to "asdf")
        val activity: Activity = mock()
        val imageFile: File = mock()
        val intent: Intent = mock()
        Mockito.`when`(permissionUtil.shouldShowRationale(eq(activity), any())).thenReturn(false)
        Mockito.`when`(permissionUtil.hasCameraPermission(activity)).doReturn(true)
        Mockito.`when`(fileUtil.createJpegImageFile(activity, "jpeg")).thenReturn(imageFile)
        Mockito.`when`(intentFactory.createTakePictureIntent(activity)).thenReturn(intent)

        photoSelector.startTakePicture(activity = activity, options = options, listener = object : PhotoSelectorListener {
            override fun onSuccessPhotoSelected(file: File) {
                assertEquals(imageFile, file)
            }

            override fun onFailurePhotoSelected(exception: PhotoSelectorException) {
                fail()
            }

        })
        photoSelector.onActivityResult(PhotoSelector.TAKE_PICTURE_REQUEST_CODE, Activity.RESULT_OK, mock())
    }

    @Test
    fun onActivityResult_selectImage_persistsImageAndCallsSuccess() {
        val contentResolver: ContentResolver = mock()
        val activity: Activity = mock {
            on { this.contentResolver } doReturn contentResolver
        }
        val intent: Intent = mock()
        val uri: Uri = mock()
        val bitmap: Bitmap = mock()
        val imageFile: File = mock()
        val mimeType = "mime"
        val dataIntent: Intent = mock {
            on { this.data } doReturn uri
        }

        Mockito.`when`(intentFactory.createSelectImageIntent(activity)).doReturn(intent)
        Mockito.`when`(mimeUtil.getMimeType(contentResolver, uri)).doReturn(mimeType)
        Mockito.`when`(bitmapUtil.getBitmap(contentResolver, uri)).doReturn(bitmap)
        Mockito.`when`(fileUtil.persistBitmap(activity, bitmap, mimeType)).doReturn(imageFile)

        photoSelector.startSelectImage(activity, options = mapOf(), listener = object : PhotoSelectorListener {
            override fun onSuccessPhotoSelected(file: File) {
                assertEquals(imageFile, file)
            }

            override fun onFailurePhotoSelected(exception: PhotoSelectorException) {
                fail()
            }

        })
        photoSelector.onActivityResult(PhotoSelector.SELECT_IMAGE_REQUEST_CODE, Activity.RESULT_OK, dataIntent)
    }

    @Test
    fun allowedRationale_setsIsRationaleShownAndWillAskPermission() {
        val options: Map<PhotoSelector.Options, Any> = mapOf(
            PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME to "asdf",
            PhotoSelector.Options.RATIONALE_HANDLER to { }
        )
        val activity: Activity = mock()
        Mockito.`when`(permissionUtil.shouldShowRationale(eq(activity), any())).thenReturn(true)
        Mockito.`when`(permissionUtil.hasCameraPermission(activity)).doReturn(false)

        photoSelector.startTakePicture(activity = activity, options = options, listener = mock())
        photoSelector.allowedRationale()

        verify(permissionUtil, times(2)).requestCameraPermissions(activity)
    }

}
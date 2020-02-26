package software.ninetofive.photoselector

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment
import software.ninetofive.photoselector.factory.DialogFactory
import software.ninetofive.photoselector.factory.FileUriFactory
import software.ninetofive.photoselector.factory.IntentFactory
import software.ninetofive.photoselector.interfaces.PhotoSelectorListener
import software.ninetofive.photoselector.util.FileUtil
import software.ninetofive.photoselector.util.PermissionUtil
import java.io.File
import javax.inject.Inject

class PhotoSelector @Inject constructor(
    private val dialogFactory: DialogFactory,
    private val permissionUtil: PermissionUtil,
    private val intentFactory: IntentFactory,
    private val fileUtil: FileUtil,
    private val fileUriFactory: FileUriFactory
) {

    private val context: Context?
        get() {
            return fragment?.context ?: activity
        }

    private var fragment: Fragment? = null
    private var activity: Activity? = null
    private lateinit var options: Map<Options, Any>
    private lateinit var listener: PhotoSelectorListener

    private var currentFile: File? = null

    fun start(activity: Activity? = null, fragment: Fragment? = null, options: Map<Options, Any>, listener: PhotoSelectorListener) {
        setup(activity, fragment, options, listener)

        if (validateOptions(activity, fragment, options)) {
            start(options)
        }
    }

    fun startTakePicture(activity: Activity? = null, fragment: Fragment? = null, options: Map<Options, Any>, listener: PhotoSelectorListener) {
        setup(activity, fragment, options, listener)

        if (validateOptions(activity, fragment, options)) {
            onTakePictureSelected()
        }
    }

    fun startSelectImage(activity: Activity? = null, fragment: Fragment? = null, options: Map<Options, Any>, listener: PhotoSelectorListener) {
        setup(activity, fragment, options, listener)
        onSelectImageSelected()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (currentFile != null) {
                currentFile?.let(listener::onSuccessPhotoSelected)
            } else {
                listener.onFailurePhotoSelected(GenericException())
            }
        } else if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let(this::handleSelectImageResult)
        } else if (resultCode != Activity.RESULT_OK) {
            listener.onFailurePhotoSelected(GenericException())
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val isPermissionGranted = permissionUtil.isPermissionGranted(requestCode, grantResults)
        if (isPermissionGranted) {
            onTakePictureSelected()
        } else {
            listener.onFailurePhotoSelected(PermissionException())
        }
    }

    fun allowedRationale() {
        permissionUtil.isRationaleShown = true
        onTakePictureSelected()
    }

    // Private functions

    private fun start(options: Map<Options, Any>) {
        val context = context ?: return
        val dialog = dialogFactory.createTypeSelectionDialog(
            context,
            if (options.containsKey(Options.TITLE_RESOURCE_ID)) options[Options.TITLE_RESOURCE_ID] as Int else R.string.dialog_select_type_title,
            if (options.containsKey(Options.TAKE_PICTURE_RESOURCE_ID)) options[Options.TAKE_PICTURE_RESOURCE_ID] as Int else R.string.dialog_select_type_option_take_picture,
            if (options.containsKey(Options.SELECT_IMAGE_RESOURCE_ID)) options[Options.SELECT_IMAGE_RESOURCE_ID] as Int else R.string.dialog_select_type_option_choose_image,
            this::onTakePictureSelected,
            this::onSelectImageSelected
        )

        dialog.show()
    }

    private fun onTakePictureSelected() {
        val context = context ?: return
        val hasPermissions = permissionUtil.hasCameraPermission(context)

        if (hasPermissions) {
            fileUtil.createJpegImageFile(context)?.let { imageFile ->
                this.currentFile = imageFile

                val takePictureIntent = intentFactory.createTakePictureIntent(context)
                val fileProviderAuthorityName = options[Options.FILE_PROVIDER_AUTHORITY_NAME] as String
                val photoUri = fileUriFactory.createUriForFile(imageFile, fileProviderAuthorityName, context)
                takePictureIntent?.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                activity?.startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE)
                fragment?.startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE)
            }
        } else {
            permissionUtil.requestCameraPermissions(activity, fragment)
        }
    }

    private fun onSelectImageSelected() {
        val context = context ?: return
        val selectImageIntent = intentFactory.createSelectImageIntent(context)

        activity?.startActivityForResult(selectImageIntent, SELECT_IMAGE_REQUEST_CODE)
        fragment?.startActivityForResult(selectImageIntent, SELECT_IMAGE_REQUEST_CODE)
    }

    private fun handleSelectImageResult(uri: Uri) {
        val context = context ?: return
        val contentResolver = context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        val type = mime.getExtensionFromMimeType(contentResolver?.getType(uri)) ?: return
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
        val file = fileUtil.persistBitmap(context, bitmap, type) ?: return

        listener.onSuccessPhotoSelected(file)
    }

    @Suppress("UNCHECKED_CAST")
    private fun setup(activity: Activity? = null, fragment: Fragment? = null, options: Map<Options, Any>, listener: PhotoSelectorListener) {
        this.activity = activity
        this.fragment = fragment
        this.listener = listener
        this.options = options

        permissionUtil.showRationale = if (options.containsKey(Options.RATIONALE_HANDLER)) options[Options.RATIONALE_HANDLER] as () -> Unit else null
    }

    private fun validateOptions(activity: Activity?, fragment: Fragment?, options: Map<Options, Any>): Boolean {
        if (options.containsKey(Options.FILE_PROVIDER_AUTHORITY_NAME)) {
            val shouldContainRationale = permissionUtil.shouldShowRationale(activity, fragment)
            if (!options.containsKey(Options.RATIONALE_HANDLER) && shouldContainRationale) {
                listener.onFailurePhotoSelected(RequiredOptionException(Options.RATIONALE_HANDLER))
            } else {
                return true
            }
        } else {
            listener.onFailurePhotoSelected(RequiredOptionException(Options.FILE_PROVIDER_AUTHORITY_NAME))
        }

        return false
    }

    enum class Options {
        TITLE_RESOURCE_ID,
        TAKE_PICTURE_RESOURCE_ID,
        SELECT_IMAGE_RESOURCE_ID,
        FILE_PROVIDER_AUTHORITY_NAME,
        RATIONALE_HANDLER
    }

    companion object {
        private const val TAKE_PICTURE_REQUEST_CODE = 533
        private const val SELECT_IMAGE_REQUEST_CODE = 555
    }

}
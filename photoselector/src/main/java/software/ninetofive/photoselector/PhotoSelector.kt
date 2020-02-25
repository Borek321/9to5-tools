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
import software.ninetofive.photoselector.util.FileUtil
import software.ninetofive.photoselector.util.PermissionUtil
import java.io.File
import java.io.IOException

class PhotoSelector(
    private val dialogFactory: DialogFactory = DialogFactory(),
    private val permissionUtil: PermissionUtil = PermissionUtil(),
    private val intentFactory: IntentFactory = IntentFactory(),
    private val fileUtil: FileUtil = FileUtil(),
    private val fileUriFactory: FileUriFactory = FileUriFactory()
) {

    private val context: Context?
        get() {
            return fragment?.context ?: activity
        }

    private var fragment: Fragment? = null
    private var activity: Activity? = null
    private lateinit var fileProviderAuthorityName: String
    private lateinit var onComplete: (File) -> Unit

    private var currentFile: File? = null

    fun start(fragment: Fragment, options: Map<Options, Any>, onComplete: (File) -> Unit) {
        this.fragment = fragment
        this.onComplete = onComplete
        start(fragment.context ?: return, options)
    }

    fun start(activity: Activity, options: Map<Options, Any>, onComplete: (File) -> Unit) {
        this.activity = activity
        this.onComplete = onComplete
        start(activity, options)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            onComplete(currentFile ?: return)
        } else if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let(this::handleSelectImageResult)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val isPermissionGranted = permissionUtil.isPermissionGranted(requestCode, grantResults)
        if (isPermissionGranted) {
            onTakePictureSelected()
        }
    }

    fun allowedRationale() {
        permissionUtil.isRationaleShown = true
        onTakePictureSelected()
    }

    // Private functions

    @Suppress("UNCHECKED_CAST")
    private fun start(context: Context, options: Map<Options, Any>) {
        val dialog = dialogFactory.createTypeSelectionDialog(
            context,
            if (options.containsKey(Options.TITLE_RESOURCE_ID)) options[Options.TITLE_RESOURCE_ID] as Int else R.string.dialog_select_type_title,
            if (options.containsKey(Options.TAKE_PICTURE_RESOURCE_ID)) options[Options.TAKE_PICTURE_RESOURCE_ID] as Int else R.string.dialog_select_type_option_take_picture,
            if (options.containsKey(Options.SELECT_IMAGE_RESOURCE_ID)) options[Options.SELECT_IMAGE_RESOURCE_ID] as Int else R.string.dialog_select_type_option_choose_image,
            this::onTakePictureSelected,
            this::onSelectImageSelected
        )
        fileProviderAuthorityName = if (options.containsKey(Options.FILE_PROVIDER_AUTHORITY_NAME)) options[Options.FILE_PROVIDER_AUTHORITY_NAME] as String else throw IOException("Please provide a file provider authority")
        permissionUtil.showRationale = if (options.containsKey(Options.RATIONALE_HANDLER)) options[Options.RATIONALE_HANDLER] as () -> Unit else null

        dialog.show()
    }

    private fun onTakePictureSelected() {
        val context = context ?: return
        val hasPermissions = permissionUtil.hasCameraPermission(context)

        if (hasPermissions) {
            fileUtil.createJpegImageFile(context)?.let { imageFile ->
                this.currentFile = imageFile

                val takePictureIntent = intentFactory.createTakePictureIntent(context)
                val photoUri = fileUriFactory.createUriForFile(imageFile, fileProviderAuthorityName, context)
                takePictureIntent?.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                activity?.startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE)
                fragment?.startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE)
            }
        } else {
            activity?.let { permissionUtil.requestCameraPermissions(it) }
                ?: fragment?.let { permissionUtil.requestCameraPermissions(it) }
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

        onComplete(file)
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
package software.ninetofive.photoselector.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import software.ninetofive.photoselector.factory.FileUriFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

open class FileUtil @Inject constructor(
    private val fireUriFactory: FileUriFactory
) {

    open fun createImageFile(context: Context, type: String = "jpeg", fileIdentifier: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())): File? {
        return try {
            val imageFileName = "JPEG_" + fileIdentifier + "_"
            val storageDirectory = getStorageDirectory(context)

            fireUriFactory.createTempFile(imageFileName, ".${type}", storageDirectory)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    open fun persistBitmap(context: Context, bitmap: Bitmap, type: String): File? {
        val file = createImageFile(context, type) ?: return null

        return try {
            val outputStream = fireUriFactory.createOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Private functions

    private fun getStorageDirectory(context: Context): File? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val dirs = context.getExternalFilesDirs(Environment.DIRECTORY_PICTURES)

            if (dirs.isNotEmpty()) {
                dirs[0]
            } else {
                null
            }
        } else {
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        }
    }

}
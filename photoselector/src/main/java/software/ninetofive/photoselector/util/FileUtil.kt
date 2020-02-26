package software.ninetofive.photoselector.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

open class FileUtil @Inject constructor() {

    open fun createJpegImageFile(context: Context, type: String = "jpeg"): File? {
        return try {
            val now = Date()
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(now)
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDirectory = getStorageDirectory(context)

            File.createTempFile(imageFileName, ".${type}", storageDirectory)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    open fun persistBitmap(context: Context, bitmap: Bitmap, type: String): File? {
        val file = createJpegImageFile(context, type) ?: return null

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }

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
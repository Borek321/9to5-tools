package software.ninetofive.tools.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.IOException
import javax.inject.Inject

class ImageRotator @Inject constructor() {

    @Throws(IOException::class)
    fun rotateImage(path: String): Bitmap {
        val bitmap = BitmapFactory.decodeFile(path)
        return rotateImage(bitmap, path)
    }

    @Throws(IOException::class)
    fun rotateImage(bitmap: Bitmap, path: String): Bitmap {
        var rotate = 0f
        val exif = ExifInterface(path)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270f
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180f
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90f
        }
        val matrix = Matrix()
        matrix.postRotate(rotate)
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width,
            bitmap.height, matrix, true
        )
    }
}
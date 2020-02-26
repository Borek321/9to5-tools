package software.ninetofive.photoselector.util

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import javax.inject.Inject

open class MimeUtil @Inject constructor() {

    open fun getMimeType(contentResolver: ContentResolver?, uri: Uri): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver?.getType(uri))
    }

}
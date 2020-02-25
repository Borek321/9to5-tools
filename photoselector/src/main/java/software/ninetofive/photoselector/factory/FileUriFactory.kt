package software.ninetofive.photoselector.factory

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.lang.IllegalArgumentException
import javax.inject.Inject

class FileUriFactory @Inject constructor() {

    fun createUriForFile(file: File, fileProviderAuthorityName: String, context: Context): Uri? {
        return try {
            FileProvider.getUriForFile(context, fileProviderAuthorityName, file)
        } catch (exception: IllegalArgumentException) {
            null
        }
    }

}
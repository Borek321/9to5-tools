package software.ninetofive.photoselector.interfaces

import software.ninetofive.photoselector.exception.PhotoSelectorException
import java.io.File

interface PhotoSelectorListener {
    fun onSuccessPhotoSelected(imageFile: File)
    fun onFailurePhotoSelected(exception: PhotoSelectorException)
}
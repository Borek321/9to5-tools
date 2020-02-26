package software.ninetofive.photoselector.exception

import software.ninetofive.photoselector.PhotoSelector

abstract class PhotoSelectorException: Exception()

class PermissionException: PhotoSelectorException() {
    override val message: String?
        get() = "Photo selection failed because permissions are not met"
}

class GenericException: PhotoSelectorException() {
    override val message: String?
        get() = "Something went wrong"
}

class RequiredOptionException(private val option: PhotoSelector.Options): PhotoSelectorException() {
    override val message: String?
        get() = "Required option ${option.name} is not provided"
}
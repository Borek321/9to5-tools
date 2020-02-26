# 9to5 Tools: Photo Selector

This library can be used to remove a lot of the boilerplate logic with taking a picture from camera or selecting an image from the gallery.

## Setup

### Gradle dependency

Add this to your module level gradle file
```
implementation software.9to5:photoselector:$photo_selector_version
```

### Implementation in your app

This library makes use of the `CAMERA` permission. Please put `<uses-permission android:name="android.permission.CAMERA"/>` in your AndroidManifest.

#### Instatiate the PhotoSelector class

##### Option 1: Inject using Dagger 2

You can inject the `PhotoSelector` class using Dagger 2.

#### Option 2: Instantiate function

If you don't use Dagger 2 or some other dependency injection library, you can also just use `PhotoSelector.newInstance()` to create a new PhotoSelector instance

#### Start the PhotoSelector

Below is a sample code snippet showing the basic usage of the Photo Selector

```
class SampleActivity: AppCompatActivity() {

    @Inject lateinit var photoSelector: PhotoSelector

    val photoSelectorOptions: Map<PhotoSelector.Options, Any> = mapOf(
        PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME to FILE_PROVIDER_AUTHORITY
    )

    val photoSelectorListener: PhotoSelectorListener = object : PhotoSelectorListener {
        override fun onFailurePhotoSelected(exception: PhotoSelectorException) {
            // Failed to select a picture
        }

        override fun onSuccessPhotoSelected(imageFile: File) {
            // Succesfully selected a photo!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoSelector.start(activity = this, options = photoSelectorOptions, listener = photoSelectorListener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        photoSelector.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        photoSelector.onActivityResult(requestCode, resultCode, data)
    }
}
```

If you don't want to use the provided dialog, you can use the specialized functions on the `PhotoSelector`:
- `startTakePicture`
- `startSelectImage`

#### Options

To make your usage of the Photo selector a bit more customized, a few options can be given:

- `TITLE_RESOURCE_ID`: Int -> The resource id that will be used as a title of the image type selection
- `TAKE_PICTURE_RESOURCE_ID`: Int -> The resource id that will be used as the "take picture" button
- `SELECT_IMAGE_RESOURCE_ID`: Int -> The resource id that will be used as the "select image" button
- `FILE_PROVIDER_AUTHORITY_NAME`: String -> The name of your file provider. To use the take picture functionality, a provider should be defined in your Manifest.
- `RATIONALE_HANDLER`: () -> Unit -> A function that is called when an activity or fragment requires to show a rationale to the user. This option is required if `[fragment or activity].shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)` returns true.

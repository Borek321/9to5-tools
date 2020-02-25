package software.ninetofive.tools

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dagger.android.support.DaggerAppCompatActivity
import software.ninetofive.photoselector.PhotoSelector
import javax.inject.Inject

class TestActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var photoSelector: PhotoSelector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authority = "$packageName.fileprovider"
        photoSelector.start(this, mapOf(
            PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME to authority,
            PhotoSelector.Options.RATIONALE_HANDLER to {
                photoSelector.allowedRationale()
            }
        )) { photo ->
            Log.e("TestActivity", "Photo taken!")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        photoSelector.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        photoSelector.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}
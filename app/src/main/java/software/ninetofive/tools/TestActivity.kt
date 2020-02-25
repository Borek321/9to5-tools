package software.ninetofive.tools

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.android.support.DaggerAppCompatActivity
import software.ninetofive.photoselector.PhotoSelector
import javax.inject.Inject

class TestActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var photoSelector: PhotoSelector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}
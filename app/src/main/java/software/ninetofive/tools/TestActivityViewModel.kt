package software.ninetofive.tools

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TestActivityViewModel : ViewModel() {

    val image: MutableLiveData<Bitmap> = MutableLiveData()

}
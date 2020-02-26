package software.ninetofive.tools

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("bitmap")
fun ImageView.setBitmap(bitmap: Bitmap?) {
    this.setImageBitmap(bitmap)
}
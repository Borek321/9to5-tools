package software.ninetofive.photoselector.factory

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import javax.inject.Inject

class IntentFactory @Inject constructor() {

    fun createTakePictureIntent(context: Context): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val canResolveIntent = intent.resolveActivity(context.packageManager) != null

        return if (canResolveIntent) intent else null
    }

    fun createSelectImageIntent(context: Context): Intent? {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val canResolveIntent = intent.resolveActivity(context.packageManager) != null

        return if (canResolveIntent) intent else null
    }

}
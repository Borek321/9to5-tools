package software.ninetofive.photoselector.factory

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import software.ninetofive.photoselector.R
import javax.inject.Inject

open class DialogFactory @Inject constructor() {

    open fun createTypeSelectionDialog(
        context: Context,
        @StringRes titleResource: Int,
        @StringRes takePictureResource: Int,
        @StringRes selectImageResource: Int,
        onTakePictureSelected: () -> Unit,
        onSelectImageSelected: () -> Unit
    ): Dialog {
        val options = arrayOf(
            context.getString(takePictureResource),
            context.getString(selectImageResource)
        )

        return MaterialAlertDialogBuilder(context)
            .setTitle(titleResource)
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        onTakePictureSelected()
                        dialog.dismiss()
                    }
                    1 -> {
                        onSelectImageSelected()
                        dialog.dismiss()
                    }
                }
            }
            .create()
    }

}
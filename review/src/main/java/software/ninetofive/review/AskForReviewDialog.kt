package software.ninetofive.review

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StyleRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import software.ninetofive.review.databinding.DialogAskForReviewBinding

open class AskForReviewDialog(
    private val context: Context,
    @StyleRes private val style: Int,
    private val title: String = context.getString(R.string.dialog_title),
    private val message: String = context.getString(R.string.dialog_message),
    private val ratingThreshold: Int = 4,
    private val onRatingClicked: OnRatingClicked
) {

    open fun create(): Dialog {
        val dialogView = createDialogView()
        return MaterialAlertDialogBuilder(ContextThemeWrapper(context, style))
            .setTitle(title)
            .setMessage(message)
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
    }

    private fun createDialogView(): View {
        val binding = DialogAskForReviewBinding.inflate(LayoutInflater.from(context), null, false)
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (rating > ratingThreshold) {
                onRatingClicked.onPositiveRating(this, rating.toInt())
            } else {
                onRatingClicked.onNegativeRating(this, rating.toInt())
            }
        }
        return binding.root
    }


    interface OnRatingClicked {
        fun onNegativeRating(dialog: AskForReviewDialog, rating: Int)
        fun onPositiveRating(dialog: AskForReviewDialog, rating: Int)
    }

}
package software.ninetofive.tools.factory

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import software.ninetofive.tools.R
import software.ninetofive.tools.databinding.DialogAskForReviewBinding
import javax.inject.Inject

class DialogFactory @Inject constructor() {

    fun createDialogView(
        context: Context,
        onPositiveFeedback: () -> Unit,
        onNegativeFeedback: () -> Unit
    ): View {
        val binding = DialogAskForReviewBinding.inflate(LayoutInflater.from(context), null, false)
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (rating > 3) onPositiveFeedback() else onNegativeFeedback()
        }
        return binding.root
    }

}
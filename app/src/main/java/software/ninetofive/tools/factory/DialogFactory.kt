package software.ninetofive.tools.factory

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import javax.inject.Inject

class DialogFactory @Inject constructor() {

    open fun createRateAppDialog(context: Context, onClickRate: (Boolean) -> Unit): Dialog {
//        val dialog = MaterialAlertDialogBuilder(ContextThemeWrapper(context, R.style.NummiTheme))
//            .setMessage(R.string.rate_app_message)
//            .setTitle(R.string.rate_app_title)
//            .setPositiveButton(R.string.rate_app_button_later) { d, _ -> d.dismiss() }
//            .create()
//        dialog.setView(createDialogView(context, {
//            dialog.dismiss()
//            onClickRate(true)
//        }) {
//            dialog.dismiss()
//            onClickRate(false)
//        })
//        return dialog

        return AlertDialog.Builder(context).create()
    }
}
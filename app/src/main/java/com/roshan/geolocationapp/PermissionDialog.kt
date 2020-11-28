package com.roshan.geolocationapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment

class PermissionDialog(
    private val listener: DialogNegativePressedListener
) : DialogFragment() {

    private val message : String = "This permission is actually necessary to get the real time position of the device."
    private val positiveBtnText : String = "Allow"
    private val negativeBtnText : String = "Deny"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(positiveBtnText) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts(
                    "package", context?.packageName,
                    null
                )
                intent.data = uri
                activity?.startActivity(intent)
                dismiss()
            }
            .setNegativeButton(negativeBtnText) { _, _ ->
                dismiss()
                listener.onNegativePressed()
            }.create()

        alertDialog.setCanceledOnTouchOutside(false)

        return alertDialog
    }
}
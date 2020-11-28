package com.roshan.geolocationapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment

class RequestDialog(
    private val listener: DialogNegativePressedListener
) : DialogFragment() {

    private val message : String = "Activate your GPS for high precision geolocation. Thank you"
    private val positiveBtnText : String = "Yes"
    private val negativeBtnText : String = "No"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(positiveBtnText) { _, _ ->
                val serviceActivityIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(serviceActivityIntent)
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
package com.roshan.geolocationapp

import android.content.Context

class PrefetenceHandler(context: Context) {

    private val PREFERENCE_NAME : String = "LocationApp Preference"
    private val USER_ASKED_LOCATION_PERMISSION : String = "First Time Permission"
    private val sharedPreferences by lazy { context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE) }

    fun permissionAskedFirstTime() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(USER_ASKED_LOCATION_PERMISSION, true)
        editor.apply()
    }

    fun getAskedPermissionFirstTimeStatus() = sharedPreferences.getBoolean(USER_ASKED_LOCATION_PERMISSION, false)

}
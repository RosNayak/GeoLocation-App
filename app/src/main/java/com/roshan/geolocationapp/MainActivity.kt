package com.roshan.geolocationapp

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), DialogNegativePressedListener {

    private lateinit var locationManager : LocationManager
    private lateinit var preference : PrefetenceHandler
    private val dialogTag : String = "Request Dialog"
    private val minTime: Long = 500
    private val minDistance: Float = 0f
    private lateinit var marker : Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setLocationManager()
        setPreference()
        fragmentGoogleMap.onCreate(savedInstanceState)
        setUpAndPointLocationOnGoogleMap()
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "On Start")
        requestAndHandlePermission()
        fragmentGoogleMap.onStart()
    }

    private fun setLocationManager() { locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private fun setPreference() { preference = PrefetenceHandler(this) }
    private fun setMapViewCallback() { fragmentGoogleMap.getMapAsync(mapViewCallback) }

    private fun requestAndHandlePermission() {
        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        Log.d("Lifecycle", "On RAHP")
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d("Lifecycle", "Not granted")
            if (
                !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                && preference.getAskedPermissionFirstTimeStatus()
            ) {
                val dialog = PermissionDialog(this)
                dialog.show(supportFragmentManager, "Permission Dialog")
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
                )
               preference.permissionAskedFirstTime()
            }
        } else {
            Log.d("Lifecycle", "Granted")
            checkForProvider()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d("Lifecycle", "On Request Permission Result.")
        if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Log.d("Lifecycle", "not granted")
            finish()
        } else {
            Log.d("Lifecycle", "granted")
            checkForProvider()
        }
    }

    private fun checkForProvider() {
        Log.d("Lifecycle", "check for provider")
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("Lifecycle", "provider not on")
            val alertDialog = RequestDialog(this)
            alertDialog.show(supportFragmentManager, dialogTag)
        } else {
            Log.d("Lifecycle", "provider on")
            setLocationListener()
        }
    }

    private fun setLocationListener() {
        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, minTime, minDistance
            ) { location ->
                Log.d("Lifecycle", "On Location Changed.")
                tvLatitude.text = location.latitude.toString()
                tvLongitude.text = location.longitude.toString()
                marker.position = getLatLngObject()
            }
        }
    }

    private fun checkForPlayServices() : Boolean {
        val googleApi = GoogleApiAvailability.getInstance()
        val isPlayServicesAvailable = googleApi.isGooglePlayServicesAvailable(this)
        if (isPlayServicesAvailable == ConnectionResult.SUCCESS) {
            return true
        } else if (googleApi.isUserResolvableError(isPlayServicesAvailable)) {
            val dialog = googleApi.getErrorDialog(this, isPlayServicesAvailable, 100, DialogInterface.OnCancelListener {
                Toast.makeText(this, "Dialog Cancelled.", Toast.LENGTH_LONG).show()
            })
            dialog.show()
            return false
        } else {
            Toast.makeText(this, "Play Services is necessary for the map.", Toast.LENGTH_LONG).show()
            return false
        }
    }

    private fun setUpAndPointLocationOnGoogleMap() {
        if (checkForPlayServices()) {
            setMapViewCallback()
        }
    }

    private val mapViewCallback = OnMapReadyCallback { googleMap ->
        googleMap.apply {
            val markerTemp = MarkerOptions()
                .position(getLatLngObject())
                .title("Your current location.")
            marker = addMarker(markerTemp)
        }
    }

    private fun getLatLngObject() : LatLng {
        val lat = tvLatitude.text.toString().toDouble()
        val lng = tvLongitude.text.toString().toDouble()
        return LatLng(lat, lng)
    }

    override fun onNegativePressed() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        fragmentGoogleMap.onResume()
    }

    override fun onStop() {
        super.onStop()
        fragmentGoogleMap.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragmentGoogleMap.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        fragmentGoogleMap.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        fragmentGoogleMap.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentGoogleMap.onDestroy()
    }
}
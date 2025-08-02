package com.example.carapp

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

class MyCarAppScreen(carContext: CarContext, private val surfaceRenderer: MapboxRenderer) :
    Screen(carContext) {

    companion object {
        private const val TAG = "CarScreen"
    }

    private var lat: Double? = null
    private var lon: Double? = null

    private val LOCATION_PERMISSIONS = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )


    override fun onGetTemplate(): Template {

        val permissionGranted = ContextCompat.checkSelfPermission(
            carContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            carContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            carContext.requestPermissions(LOCATION_PERMISSIONS) { granted, denied ->
                if (LOCATION_PERMISSIONS.all { it in granted }) {
                    invalidate()
                } else {
                    CarToast.makeText(carContext, R.string.permission_denied, CarToast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        if (lat == null || lon == null) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(carContext)

            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    Log.d(TAG, "Location: $lat , $lon")
                    invalidate()
                } else {
                    lat = 28.6139
                    lon = 77.2090
                    Log.d(TAG, carContext.getString(R.string.fallback_default_location))
                    invalidate()
                }
            }

            return PaneTemplate.Builder(
                Pane.Builder()
                    .addRow(
                        Row.Builder().setTitle(carContext.getString(R.string.fetching_location))
                            .build()
                    )
                    .build()
            ).build()
        }

        val currentLat = lat ?: 28.6139
        val currentLon = lon ?: 77.2090
        Log.d(TAG, "Fetching Location $currentLat, $currentLon")
        surfaceRenderer.updateUserLocation(currentLat, currentLon)


        return PaneTemplate.Builder(
            Pane.Builder()
                .addRow(
                    Row.Builder().setTitle(carContext.getString(R.string.app_name)).build()
                )
                .build()
        ).build()


    }
}
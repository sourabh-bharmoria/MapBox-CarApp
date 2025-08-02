package com.example.carapp

import android.app.Presentation
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.os.Bundle
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.locationcomponent.location

class MapboxRenderer(
    private val carContext: CarContext,
    lifecycle: Lifecycle
) : DefaultLifecycleObserver {

    companion object{
        private const val TAG = "MapBoxRenderer"
    }

    private var mapView: MapView? = null
    private var newLocation: Point? = null
    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var presentation: Presentation

    val surfaceCallback = object : SurfaceCallback {

        override fun onSurfaceAvailable(surfaceContainer: SurfaceContainer) {
            val displayManager = carContext.getSystemService(DisplayManager::class.java)!!
            virtualDisplay = displayManager.createVirtualDisplay(
                "mapbox_virtual_display",
                surfaceContainer.width,
                surfaceContainer.height,
                surfaceContainer.dpi,
                surfaceContainer.surface,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
            )
            Log.d(TAG, "Before Presentation")

            val context = carContext.createDisplayContext(virtualDisplay.display)

            presentation = object : Presentation(context, virtualDisplay.display) {
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    Log.d(TAG, "Map Loading....")
                    val mapInitOptions = MapInitOptions(context = context)

                    mapView = MapView(context, mapInitOptions)
                    setContentView(mapView!!)
                    Log.d(TAG, "Map Loaded")



                    mapView?.mapboxMap?.loadStyle(
                        style(Style.MAPBOX_STREETS) {}
                    ) {
                        mapView?.location?.updateSettings {
                            enabled = true
                            pulsingEnabled = true
                        }
                        Log.d(TAG, "Calling new point $newLocation")
                        newLocation?.let { point ->
                            mapView?.mapboxMap?.setCamera(
                                CameraOptions.Builder()
                                    .center(point)
                                    .zoom(15.0)
                                    .build()
                            )
                        }
                    }
                }
            }
            presentation.show()
        }

        override fun onSurfaceDestroyed(container: SurfaceContainer) {
            mapView?.onStop()
            mapView?.onDestroy()
            virtualDisplay.release()
            presentation.dismiss()
        }
    }

    init {
        lifecycle.addObserver(this)
    }

    fun updateUserLocation(lat: Double, lon: Double) {
        Log.d(TAG, "Location $lat, $lon")
        newLocation = Point.fromLngLat(lon, lat)
        mapView?.mapboxMap?.setCamera(
            CameraOptions.Builder()
                .center(newLocation)
                .zoom(15.0)
                .build()
        )
    }
}

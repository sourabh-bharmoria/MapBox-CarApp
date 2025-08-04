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
import androidx.lifecycle.LifecycleOwner
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.Plugin
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.Job

class MapboxRenderer(
    private val carContext: CarContext,
    lifecycle: Lifecycle
) : DefaultLifecycleObserver {

    companion object{
        private const val TAG = "MapBoxRenderer"
        private const val VIRTUAL_DISPLAY_NAME = "mapbox_virtual_display"
    }

    private var mapView: MapView? = null
    private var newLocation: Point? = null
    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var presentation: Presentation
    private var customLifecycleOwner: CustomLifecycleOwner? = null


    val surfaceCallback = object : SurfaceCallback {

        override fun onSurfaceAvailable(surfaceContainer: SurfaceContainer) {
            Log.d(TAG, carContext.getString(R.string.surface_available))

            MapboxOptions.accessToken = carContext.getString(R.string.public_token)

            val displayManager = carContext.getSystemService(DisplayManager::class.java)!!
            virtualDisplay = displayManager.createVirtualDisplay(
                VIRTUAL_DISPLAY_NAME,
                surfaceContainer.width,
                surfaceContainer.height,
                surfaceContainer.dpi,
                surfaceContainer.surface,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
            )


            presentation = object : Presentation(carContext, virtualDisplay.display, R.style.Theme_CarApp) {
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    Log.d(TAG, carContext.getString(R.string.map_loading))

                    val filteredPlugins = MapInitOptions.defaultPluginList.filterNot {
                        it.id == Plugin.MAPBOX_LIFECYCLE_PLUGIN_ID
                    }

                    val mapInitOptions = MapInitOptions(context, plugins = filteredPlugins)

                    mapView = MapView(context, mapInitOptions)


                    mapView?.isClickable = true
                    mapView?.isFocusable = true
                    mapView?.isFocusableInTouchMode = true
                    mapView?.requestFocus()
                    setContentView(mapView!!)

                    mapView?.onStart()

                    Log.d(TAG, carContext.getString(R.string.map_loaded))



                    mapView?.mapboxMap?.loadStyle(
                        style(Style.MAPBOX_STREETS) {}
                    ) {
                        mapView?.location?.updateSettings {
                            enabled = true
                            pulsingEnabled = true
                            locationPuck = LocationPuck2D()
                        }

                        mapView?.gestures?.apply {
                            scrollEnabled = true
                            rotateEnabled = true
                            pinchToZoomEnabled = true
                            doubleTapToZoomInEnabled = true
                            quickZoomEnabled = true
                        }
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

//        override fun onScroll(distanceX: Float, distanceY: Float) {
//            valueAnimator?.cancel()
//            val map = mapView?.mapboxMap ?: return
//            val screenCenter = map.pixelForCoordinate(map.cameraState.center)
//
//            val newScreenCenter = ScreenCoordinate(
//                screenCenter.x + distanceX,
//                screenCenter.y + distanceY
//            )
//
//            val newMapCenter = map.coordinateForPixel(newScreenCenter)
//
//            map.setCamera(
//                CameraOptions.Builder()
//                    .center(newMapCenter)
//                    .zoom(15.0)
//                    .build()
//            )
//        }
//
//        override fun onScale(focusX: Float, focusY: Float, scaleFactor: Float) {
//            val map = mapView?.mapboxMap ?: return
//            if(scaleFactor == 2f) return
//
//            val offsetX = (focusX - mapView!!.width / 2) * (scaleFactor - 1f)
//            val offsetY = (focusY - mapView!!.height / 2) * (scaleFactor - 1f)
//
//            val camera = map.cameraState
//            val currentZoom = camera.zoom
//            val currentCenter = camera.center
//
//            val newZoom = currentZoom + (scaleFactor - 1f) * 2
//
//            val screenX = map.pixelForCoordinate(currentCenter).x - offsetX
//            val screenY = map.pixelForCoordinate(currentCenter).y - offsetY
//            val newCenter = map.coordinateForPixel(ScreenCoordinate(screenX, screenY))
//
//            map.setCamera(
//                CameraOptions.Builder()
//                    .center(newCenter)
//                    .zoom(newZoom)
//                    .build()
//            )
//
//        }
    }


    init {
        lifecycle.addObserver(this)
    }

    fun updateUserLocation(lat: Double, lon: Double) {
        Log.d(TAG, carContext.getString(R.string.location))
        newLocation = Point.fromLngLat(lon, lat)

        mapView?.mapboxMap?.setCamera(
            CameraOptions.Builder()
                .center(newLocation)
                .zoom(15.0)
                .build()
        )
    }


}

//package com.example.carapp
//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.util.Log
//import androidx.car.app.CarContext
//import androidx.car.app.CarToast
//import androidx.car.app.Screen
//import androidx.car.app.model.Action
//import androidx.car.app.model.ActionStrip
//import androidx.car.app.model.CarColor
//import androidx.car.app.navigation.model.NavigationTemplate
//import androidx.car.app.navigation.model.RoutingInfo
//import androidx.core.content.ContextCompat
//import com.mapbox.geojson.Point
//import com.mapbox.navigation.base.options.NavigationOptions
//import com.mapbox.navigation.base.trip.model.RouteProgress
//import com.mapbox.navigation.core.MapboxNavigation
//import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
//import com.mapbox.navigation.ui.base.lifecycle.UIBind
//import com.mapbox.navigation.ui.maps.NavigationStyles
//import com.mapbox.navigation.ui.maps.car.CarMapSurfaceProvider
//import com.mapbox.navigation.ui.maps.car.map.MapboxCarMap
//import com.mapbox.navigation.ui.maps.car.map.OnCarMapReadyListener
//import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
//
//class MyCarAppScreen(
//    carContext: CarContext
//) : Screen(carContext) {
//
//    companion object {
//        private const val TAG = "CarMapboxScreen"
//    }
//
//    private var mapboxCarMap: MapboxCarMap? = null
//    private val navigationLocationProvider = NavigationLocationProvider()
//    private val permissions = listOf(
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION
//    )
//
//    private var isInitialized = false
//
//    override fun onGetTemplate(): NavigationTemplate {
//        // Check location permission
//        val permissionsGranted = permissions.all {
//            ContextCompat.checkSelfPermission(carContext, it) == PackageManager.PERMISSION_GRANTED
//        }
//
//        if (!permissionsGranted) {
//            carContext.requestPermissions(permissions) { granted, denied ->
//                if (permissions.all { it in granted }) {
//                    CarToast.makeText(carContext, "Location granted", CarToast.LENGTH_SHORT).show()
//                    invalidate()
//                } else {
//                    CarToast.makeText(carContext, "Location permission denied", CarToast.LENGTH_LONG).show()
//                }
//            }
//
//            return NavigationTemplate.Builder()
//                .setBackgroundColor(CarColor.PRIMARY)
//                .setNavigationInfo(
//                    RoutingInfo.Builder()
//                        .setCurrentRoad("Requesting location permission…")
//                        .build()
//                )
//                .build()
//        }
//
//        // Initialize MapboxNavigation if not already
//        if (!isInitialized) {
//            MapboxNavigationApp.setup(
//                NavigationOptions.Builder(carContext)
//                    .accessToken(carContext.getString(R.string.public_token))
//                    .build()
//            )
//            isInitialized = true
//        }
//
//        val carMapSurface = CarMapSurfaceProvider.getSurface(carContext)
//
//        if (carMapSurface != null && mapboxCarMap == null) {
//            mapboxCarMap = MapboxCarMap.create(carContext, carMapSurface)
//
//            mapboxCarMap?.registerObserver(object : OnCarMapReadyListener {
//                override fun onCarMapReady() {
//                    mapboxCarMap?.mapboxMap?.loadStyleUri(NavigationStyles.NAVIGATION_DAY_STYLE) {
//                        mapboxCarMap?.mapboxMap?.setCamera(
//                            com.mapbox.maps.CameraOptions.Builder()
//                                .zoom(14.0)
//                                .center(Point.fromLngLat(77.2090, 28.6139)) // Delhi
//                                .build()
//                        )
//                        mapboxCarMap?.location?.setLocationProvider(navigationLocationProvider)
//                        mapboxCarMap?.location?.render()
//                    }
//                }
//
//                override fun onAttached(mapboxCarMap: MapboxCarMap) {}
//                override fun onDetached(mapboxCarMap: MapboxCarMap) {}
//            })
//        }
//
//        return NavigationTemplate.Builder()
//            .setBackgroundColor(CarColor.BLUE)
//            .setNavigationInfo(
//                RoutingInfo.Builder()
//                    .setCurrentRoad("Mapbox Navigation Ready")
//                    .build()
//            )
//            .setActionStrip(
//                ActionStrip.Builder()
//                    .addAction(
//                        Action.Builder()
//                            .setTitle("Options")
//                            .setOnClickListener {
//                                CarToast.makeText(carContext, "Options clicked", CarToast.LENGTH_SHORT).show()
//                            }
//                            .build()
//                    )
//                    .build()
//            )
//            .build()
//    }
//}

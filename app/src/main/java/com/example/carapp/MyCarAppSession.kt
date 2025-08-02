package com.example.carapp

import android.content.Intent
import androidx.car.app.AppManager
import androidx.car.app.Screen
import androidx.car.app.Session

class MyCarAppSession: Session() {


    override fun onCreateScreen(intent: Intent): Screen {

        val surfaceRenderer = MapboxRenderer(carContext, lifecycle)


        carContext.getCarService(AppManager::class.java).setSurfaceCallback(surfaceRenderer.surfaceCallback)

        return MyCarAppScreen(carContext, surfaceRenderer)
    }
}
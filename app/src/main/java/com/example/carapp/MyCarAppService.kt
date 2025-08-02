package com.example.carapp

import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

class MyCarAppService: androidx.car.app.CarAppService() {
    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        return MyCarAppSession()
    }

}
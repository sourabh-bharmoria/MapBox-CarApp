package com.example.carapp

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class CustomLifecycleOwner : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    fun handleOnCreate() {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun handleOnStart() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    fun handleOnResume() {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    fun handleOnPause() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    fun handleOnStop() {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun handleOnDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }
}

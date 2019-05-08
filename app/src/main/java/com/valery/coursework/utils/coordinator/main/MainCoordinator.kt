package com.valery.coursework.utils.coordinator.main

import com.valery.coursework.utils.coordinator.Coordinator

interface MainCoordinator : Coordinator {
    fun openMenu()

    fun openImageDetection()

    fun openImagePicker()
}
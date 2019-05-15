package com.valery.coursework.ui.coordinator.main

import androidx.appcompat.app.AppCompatActivity
import com.valery.coursework.ui.coordinator.Coordinator

interface MainCoordinator : Coordinator {
    fun initMainCoordinatorWith(activity: AppCompatActivity)

    fun openMenu()

    fun openImageDetection()

    fun openImagePicker()
}
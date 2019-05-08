package com.valery.coursework.utils.coordinator.main

import androidx.appcompat.app.AppCompatActivity
import com.valery.coursework.utils.coordinator.Coordinator

interface MainCoordinator : Coordinator {
    fun initMainCoordinatorWith(activity: AppCompatActivity)

    fun openMenu()

    fun openImageDetection()

    fun openImagePicker()
}
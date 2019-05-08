package com.valery.coursework

import android.os.Bundle
import com.valery.coursework.ui.base.BaseFragmentActivity
import com.valery.coursework.utils.coordinator.main.MainCoordinator
import com.valery.coursework.utils.coordinator.main.MainCoordinatorImpl


class MainActivity : BaseFragmentActivity(), MainCoordinator {

    override val layoutId: Int = R.layout.activity_main

    override val containerId: Int = R.id.flContent

    private val coordinator: MainCoordinator by lazy {
        MainCoordinatorImpl(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            coordinator.openMenu()
        }
    }

    override fun openImageDetection() {
        coordinator.openImageDetection()
    }

    override fun openImagePicker() {
        coordinator.openImagePicker()
    }

    override fun openMenu() {
        coordinator.openMenu()
    }
}
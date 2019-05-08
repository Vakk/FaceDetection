package com.valery.coursework.utils.coordinator.main

import androidx.appcompat.app.AppCompatActivity
import com.valery.coursework.R
import com.valery.coursework.ui.main.detection.camera.CameraDetectionFragment
import com.valery.coursework.ui.main.detection.image.ImageDetectionFragment
import com.valery.coursework.ui.main.menu.MenuFragment
import com.valery.coursework.utils.coordinator.BaseFragmentCoordinator

class MainCoordinatorImpl : BaseFragmentCoordinator(), MainCoordinator {

    override fun initMainCoordinatorWith(activity: AppCompatActivity) {
        containerId = R.id.flContent
        fragmentManager = activity.supportFragmentManager
    }

    override fun openMenu() {
        navigationHelper.popToRootFragment()
        replaceFragment(MenuFragment.newInstance())
    }

    override fun openImageDetection() {
        replaceFragment(CameraDetectionFragment.newInstance())
    }


    override fun openImagePicker() {
        replaceFragment(ImageDetectionFragment.newInstance())
    }

}
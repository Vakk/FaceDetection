package com.valery.coursework.utils.coordinator.main

import com.valery.coursework.MainActivity
import com.valery.coursework.R
import com.valery.coursework.ui.main.detection.camera.CameraDetectionFragment
import com.valery.coursework.ui.main.detection.image.ImageDetectionFragment
import com.valery.coursework.ui.main.menu.MenuFragment
import com.valery.coursework.utils.coordinator.BaseFragmentCoordinator

class MainCoordinatorImpl(activity: MainActivity) :
    BaseFragmentCoordinator(R.id.flContent, activity.supportFragmentManager),
    MainCoordinator {

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
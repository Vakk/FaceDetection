package com.valery.coursework

import android.os.Bundle
import com.valery.coursework.ui.base.BaseFragmentActivity
import com.valery.coursework.ui.main.detection.image.ImageDetectionFragment


class MainActivity : BaseFragmentActivity() {

    override val layoutId: Int = R.layout.activity_main

    override val containerId: Int = R.id.flContent

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        replaceFragment(ImageDetectionFragment.newInstance())
    }
}
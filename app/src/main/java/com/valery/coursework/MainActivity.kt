package com.valery.coursework

import android.os.Bundle
import com.valery.coursework.ui.base.BaseActivity
import com.valery.coursework.utils.coordinator.main.MainCoordinator
import com.valery.coursework.utils.coordinator.main.MainCoordinatorImpl


class MainActivity : BaseActivity(), MainCoordinator by MainCoordinatorImpl() {

    override val layoutId: Int = R.layout.activity_main

    init {
        initMainCoordinatorWith(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            openMenu()
        }
    }

}
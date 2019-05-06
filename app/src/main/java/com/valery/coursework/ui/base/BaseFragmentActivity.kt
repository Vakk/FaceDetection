package com.valery.coursework.ui.base

import androidx.fragment.app.Fragment

abstract class BaseFragmentActivity : BaseActivity() {
    abstract val containerId: Int

    open fun replaceFragment(
            fragment: Fragment,
            containerId: Int = this.containerId,
            tag: String = fragment.javaClass.name
    ) {
        supportFragmentManager.beginTransaction().apply {
            addToBackStack(tag)
            replace(containerId, fragment, tag)
            commit()
        }
    }

}
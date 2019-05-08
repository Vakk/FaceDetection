package com.valery.coursework.ui.base

import androidx.fragment.app.Fragment
import com.valery.coursework.utils.navigation.FragmentNavigationHelperImpl

abstract class BaseFragmentActivity : BaseActivity() {
    abstract val containerId: Int

    protected val navigationHelper by lazy { FragmentNavigationHelperImpl(containerId, supportFragmentManager) }

    open fun replaceFragment(
        fragment: Fragment,
        containerId: Int = this.containerId,
        tag: String = fragment.javaClass.name
    ) {
        navigationHelper.replaceFragment(
            fragment,
            containerId = containerId,
            name = tag,
            animInFirst = null,
            animInSecond = null,
            animOutSecond = null,
            animOutFirst = null,
            needToReplace = false,
            forceUpdateFragment = true,
            needToAddToBackStack = true
        )
    }

}
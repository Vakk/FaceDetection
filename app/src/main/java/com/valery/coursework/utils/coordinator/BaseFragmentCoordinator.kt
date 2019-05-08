package com.valery.coursework.utils.coordinator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.valery.coursework.utils.navigation.FragmentNavigationHelper
import com.valery.coursework.utils.navigation.FragmentNavigationHelperImpl

abstract class BaseFragmentCoordinator(var containerId: Int, fragmentManager: FragmentManager) : Coordinator {

    protected val navigationHelper: FragmentNavigationHelper by lazy {
        FragmentNavigationHelperImpl(containerId, fragmentManager)
    }

    fun replaceFragment(
        fragment: Fragment,
        containerId: Int = this.containerId,
        needToReplace: Boolean = false
    ) {
        navigationHelper.replaceFragment(
            fragment = fragment,
            needToAddToBackStack = true,
            forceUpdateFragment = true,
            needToReplace = needToReplace,
            animInFirst = null,
            animInSecond = null,
            animOutFirst = null,
            animOutSecond = null,
            name = fragment.javaClass.simpleName,
            containerId = containerId
        )
    }


}
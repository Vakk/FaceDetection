package com.valery.coursework.utils.navigation

import androidx.fragment.app.Fragment

interface FragmentNavigationHelper {

    val containerId: Int

    fun replaceFragment(
        fragment: Fragment,
        needToAddToBackStack: Boolean,
        forceUpdateFragment: Boolean,
        needToReplace: Boolean,
        containerId: Int = this.containerId,
        animInFirst: Int?,
        animInSecond: Int?,
        animOutFirst: Int?,
        animOutSecond: Int?,
        name: String = fragment.javaClass.simpleName
    )

    fun popLastFragment()

    fun popToRootFragment()
}
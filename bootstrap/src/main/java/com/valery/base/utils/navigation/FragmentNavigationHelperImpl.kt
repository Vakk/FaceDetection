package com.valery.base.utils.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentNavigationHelperImpl(
    override var containerId: Int,
    var fragmentManager: FragmentManager?
) : FragmentNavigationHelper {

    override fun replaceFragment(
        fragment: Fragment,
        needToAddToBackStack: Boolean,
        forceUpdateFragment: Boolean,
        needToReplace: Boolean,
        containerId: Int,
        animInFirst: Int?,
        animInSecond: Int?,
        animOutFirst: Int?,
        animOutSecond: Int?,
        name: String
    ) {
        val fragmentManager = fragmentManager ?: return
        val fragment = if (forceUpdateFragment) fragment else fragmentManager.findFragmentByTag(name)
            ?: fragment

        with(fragmentManager.beginTransaction()) {
            if (animInFirst != null && animInSecond != null) {
                if (animOutFirst != null && animOutSecond != null) {
                    setCustomAnimations(animInFirst, animInSecond, animOutFirst, animOutSecond)
                } else {
                    setCustomAnimations(animInFirst, animInSecond)
                }
            }

            if (needToReplace) {
                replace(containerId, fragment, name)
            } else {
                add(containerId, fragment, name)
            }
            if (needToAddToBackStack) {
                addToBackStack(name)
            }
            commitAllowingStateLoss()
        }
    }

    override fun popLastFragment() {
        fragmentManager?.popBackStack()
    }

    override fun popToRootFragment() {
        val fragmentManager = fragmentManager ?: return
        for (index in fragmentManager.fragments.count() downTo 0) {
            fragmentManager.popBackStack()
        }
    }


}
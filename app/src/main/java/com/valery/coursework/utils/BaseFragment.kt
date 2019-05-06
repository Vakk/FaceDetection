package com.valery.coursework.utils

import com.google.android.material.snackbar.Snackbar
import com.valery.coursework.ui.base.BaseFragment
import com.valery.coursework.ui.base.message.MessageView

fun <T> T.showMessage(message: String) where T : BaseFragment<*>, T : MessageView {
    val rootView = rootView ?: return
    showSnackbar(message, rootView, Snackbar.LENGTH_SHORT)
}
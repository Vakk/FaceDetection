package com.valery.base.utils.extensions

import com.google.android.material.snackbar.Snackbar
import com.valery.base.BaseFragment
import com.valery.base.message.MessageView

fun <T> T.showMessage(message: String) where T : BaseFragment<*>, T : MessageView {
    val rootView = rootView ?: return
    showSnackbar(message, rootView, Snackbar.LENGTH_SHORT)
}
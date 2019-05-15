package com.valery.base.message

import android.view.View
import com.google.android.material.snackbar.Snackbar

interface MessageView {
    fun showToast(message: String)

    fun showToast(id: Int)

    fun showSnackbar(message: String, view: View, length: Int = Snackbar.LENGTH_SHORT)

    fun showSnackbar(id: Int, view: View, length: Int = Snackbar.LENGTH_SHORT)
}
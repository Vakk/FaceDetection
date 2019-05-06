package com.valery.coursework.ui.base.message

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference

class MessageViewImpl(context: Context) : MessageView {

    private val contextWR = WeakReference(context)
    private val context: Context?
        get() = contextWR.get()

    override fun showToast(message: String) {
        val context = context ?: return
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showToast(id: Int) {
        val context = context ?: return
        showToast(context.getString(id))
    }

    override fun showSnackbar(message: String, view: View, length: Int) {
        Snackbar.make(view, message, length)
    }

    override fun showSnackbar(id: Int, view: View, length: Int) {
        val context = context ?: return
        showSnackbar(context.getString(id), view, length)
    }
}
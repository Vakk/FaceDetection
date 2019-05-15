package com.valery.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.valery.base.message.MessageView
import com.valery.base.message.MessageViewImpl

abstract class BaseActivity : AppCompatActivity(), MessageView {

    abstract val layoutId: Int

    protected open val messageView: MessageView? by lazy {
        MessageViewImpl(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
    }

    override fun showToast(message: String) {
        messageView?.showToast(message)
    }

    override fun showToast(id: Int) {
        messageView?.showToast(id)
    }

    override fun showSnackbar(message: String, view: View, length: Int) {
        messageView?.showSnackbar(message, view, length)
    }

    override fun showSnackbar(id: Int, view: View, length: Int) {
        messageView?.showSnackbar(id, view, length)
    }
}
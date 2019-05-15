package com.valery.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.valery.base.message.MessageView
import com.valery.base.utils.extensions.cast

abstract class BaseFragment<T : BaseViewModel>(private val clazz: Class<T>) : Fragment(), MessageView {
    lateinit var viewModel: T

    abstract val layoutId: Int

    var rootView: View? = null

    protected open var messageView: MessageView? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        messageView = context.cast()
    }

    override fun onDetach() {
        messageView = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutId, container, false)
        rootView = view
        return view
    }

    override fun onDestroyView() {
        rootView = null
        super.onDestroyView()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this)[clazz]
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
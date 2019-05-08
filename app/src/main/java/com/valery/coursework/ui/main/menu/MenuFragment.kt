package com.valery.coursework.ui.main.menu

import android.content.Context
import android.os.Bundle
import android.view.View
import com.valery.coursework.R
import com.valery.coursework.ui.base.BaseFragment
import com.valery.coursework.utils.coordinator.main.MainCoordinator
import kotlinx.android.synthetic.main.fragment_image_detection.*
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : BaseFragment<MenuViewModel>(MenuViewModel::class.java) {

    override val layoutId: Int = R.layout.fragment_menu

    private var mainCoordinator: MainCoordinator? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mainCoordinator = context as MainCoordinator
    }

    override fun onDetach() {
        mainCoordinator = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCamera.setOnClickListener { openCameraView() }
        btnImage.setOnClickListener { openPickImageView() }
    }

    fun openCameraView() {
        mainCoordinator?.openImageDetection()
    }

    fun openPickImageView() {
        mainCoordinator?.openImagePicker()
    }

    companion object {
        fun newInstance(): MenuFragment {
            return MenuFragment()
        }
    }
}
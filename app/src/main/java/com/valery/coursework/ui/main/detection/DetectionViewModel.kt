package com.valery.coursework.ui.main.detection

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.valery.coursework.ui.base.BaseViewModel

class DetectionViewModel : BaseViewModel() {
    val onBitmapLoaded = MutableLiveData<Bitmap>()
}
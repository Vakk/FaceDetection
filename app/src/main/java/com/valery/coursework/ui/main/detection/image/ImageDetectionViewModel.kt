package com.valery.coursework.ui.main.detection.image

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.valery.coursework.ui.base.BaseViewModel

class ImageDetectionViewModel : BaseViewModel() {
    val onBitmapLoaded = MutableLiveData<Bitmap>()
}
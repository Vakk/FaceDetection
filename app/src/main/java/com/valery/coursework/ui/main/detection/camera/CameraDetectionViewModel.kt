package com.valery.coursework.ui.main.detection.camera

import android.graphics.Bitmap
import android.media.ImageReader
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.valery.base.BaseViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class CameraDetectionViewModel : BaseViewModel() {
    val onFrameChanged = MutableLiveData<Bitmap>()

    private val imageReaderSubject = PublishSubject.create<ImageReader>()

    init {
        prepareBitmapFlowable()
    }

    fun processImageReader(imageReader: ImageReader) {
        imageReaderSubject.onNext(imageReader)
    }

    private fun prepareBitmapFlowable() {
        imageReaderSubject
            .toFlowable(BackpressureStrategy.DROP)
            .map {
                it.acquireNextImage().use { image ->
                    val firebaseFace = FirebaseVisionImage.fromMediaImage(
                        image,
                        FirebaseVisionImageMetadata.ROTATION_270
                    )
                    firebaseFace.bitmapForDebugging
                }
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
//                onFrameChanged.value = it
            }
            .addToBag()
    }
}
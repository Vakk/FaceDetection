package com.valery.coursework.ui.main.detection.image

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.graphics.applyCanvas
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.valery.coursework.R
import com.valery.coursework.ui.base.BaseFragment
import com.valery.coursework.utils.RequestCode
import com.valery.coursework.utils.extensions.showMessage
import kotlinx.android.synthetic.main.fragment_image_detection.*


class ImageDetectionFragment : BaseFragment<ImageDetectionViewModel>(ImageDetectionViewModel::class.java),
    View.OnClickListener {

    override val layoutId: Int = R.layout.fragment_image_detection

    val highAccuracyOpts = FirebaseVisionFaceDetectorOptions.Builder()
        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
        .build()

    private val staticDetector by lazy {
        FirebaseVision.getInstance().getVisionFaceDetector(highAccuracyOpts)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnChooseImage.setOnClickListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (RequestCode.toRequestCode(requestCode)) {
            RequestCode.PICK_IMAGE -> {
                val allPermissionsGranted = grantResults.firstOrNull { it == PackageManager.PERMISSION_DENIED } == null
                if (allPermissionsGranted) {
                    pickImageForce()
                } else {
                    showMessage("You should allow permissions.")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.PICK_IMAGE.code -> {
                data?.data?.let { detectStaticImage(it) }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnChooseImage -> pickImage()
        }
    }

    private fun pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), RequestCode.PICK_IMAGE.code)
        } else {
            pickImageForce()
        }
    }

    private fun pickImageForce() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, RequestCode.PICK_IMAGE.code)
    }

    private fun detectStaticImage(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
        staticDetector.detectInImage(FirebaseVisionImage.fromBitmap(bitmap))?.addOnSuccessListener {
            processFaces(bitmap.copy(Bitmap.Config.ARGB_8888, true), it)
        }
    }

    private fun processFaces(mutableBitmap: Bitmap, faces: List<FirebaseVisionFace>) {
        mutableBitmap.applyCanvas {
            for (face in faces) {
                drawRect(face.boundingBox, Paint().apply {
                    color = Color.CYAN
                    style = Paint.Style.STROKE
                })
//                if (face.leftEyeOpenProbability > 0.9f) {
//                    showMessage("Start music")
//                } else {
//                    showMessage("No commands found")
//                }
            }
        }
        ivImage.setImageBitmap(mutableBitmap)
    }


    companion object {
        fun newInstance(): ImageDetectionFragment =
            ImageDetectionFragment()

        /*
    fun initML() {
        // High-accuracy landmark detection and face classification


        // Real-time contour detection of multiple faces
        val realTimeOpts = FirebaseVisionFaceDetectorOptions.Builder()
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .build()
    }

*/
    }

}
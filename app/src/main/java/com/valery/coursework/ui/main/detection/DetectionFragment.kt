package com.valery.coursework.ui.main.detection

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.valery.coursework.utils.showMessage
import kotlinx.android.synthetic.main.fragment_detection.*


class DetectionFragment : BaseFragment<DetectionViewModel>(DetectionViewModel::class.java), View.OnClickListener {

    override val layoutId: Int = R.layout.fragment_detection

    private val realTimeOpts by lazy {
        FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()
    }

    private val staticOpts by lazy {
        FirebaseVisionFaceDetectorOptions.Builder()
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .build()
    }

    private val imageDetector by lazy {
        FirebaseVision.getInstance().getVisionFaceDetector(realTimeOpts)
    }

    private val staticDetector by lazy {
        FirebaseVision.getInstance().getVisionFaceDetector(staticOpts)
    }

    private var cameraManager: CameraManager? = null
    private var cameraDevice: CameraDevice? = null

    private var cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onDisconnected(camera: CameraDevice) {
            showMessage("Camera disconnected.")
        }

        override fun onError(camera: CameraDevice, error: Int) {
            showMessage("Camera error.")
        }

        override fun onOpened(camera: CameraDevice) {
            showMessage("Camera opened.")
            cameraDevice = camera
            configureCamera()
        }
    }

    var sessionCallback: CameraCaptureSession.StateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            val cameraDevice = cameraDevice ?: return
            val requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            requestBuilder.addTarget(svCamera.holder.surface)
            session.setRepeatingRequest(requestBuilder.build(), null, null)
            showMessage("capture session configured: $session")
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            showMessage("capture session configure failed: $session")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnChooseImage.setOnClickListener(this)
        runCamera()
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
            RequestCode.RUN_CAMERA -> {
                val allPermissionsGranted = grantResults.firstOrNull { it == PackageManager.PERMISSION_DENIED } == null
                if (allPermissionsGranted) {
                    runCameraForce()
                } else {
                    showMessage("You should allow access to camera.")
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

    private fun runCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), RequestCode.RUN_CAMERA.code)
        } else {
            runCameraForce()
        }
    }

    private fun runCameraForce() {
        cameraManager = activity?.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        val cameraManager = cameraManager ?: return
        val cameraIdList = cameraManager.cameraIdList
        try {
            cameraManager.openCamera(cameraIdList[1], cameraStateCallback, Handler())
        } catch (e: SecurityException) {
            showMessage("Camera's permissions not allowed.")
        }
    }

    private fun configureCamera() {
        val cameraDevice = cameraDevice ?: return
        val surface = svCamera.holder.surface
        try {
            cameraDevice.createCaptureSession(listOf(surface), sessionCallback, Handler())
        } catch (e: CameraAccessException) {
            showMessage(e.toString())
        }
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
                if (face.leftEyeOpenProbability > 0.9f) {
                    showMessage("Start music")
                }
            }
        }
        ivImage.setImageBitmap(mutableBitmap)
    }


    companion object {
        fun newInstance(): DetectionFragment = DetectionFragment()

        /*
    fun initML() {
        // High-accuracy landmark detection and face classification
        val highAccuracyOpts = FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build()

        // Real-time contour detection of multiple faces
        val realTimeOpts = FirebaseVisionFaceDetectorOptions.Builder()
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .build()
    }

*/
    }

}
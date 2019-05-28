package com.valery.coursework.ui.main.detection.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Paint
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.valery.base.BaseFragment
import com.valery.base.utils.RequestCode
import com.valery.base.utils.extensions.showMessage
import com.valery.coursework.R
import kotlinx.android.synthetic.main.fragment_camera_detection.*

class CameraDetectionFragmentNew : BaseFragment<CameraDetectionViewModel>(CameraDetectionViewModel::class.java),
    ImageReader.OnImageAvailableListener {

    companion object {
        fun newInstance(): CameraDetectionFragmentNew {
            return CameraDetectionFragmentNew()
        }
    }

    override val layoutId: Int = R.layout.fragment_camera_detection

    private val realTimeOpts by lazy {
        FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .build()
    }

    private val cameraDetector by lazy {
        FirebaseVision.getInstance().getVisionFaceDetector(realTimeOpts)
    }
    private var cameraManager: CameraManager? = null
    private var imageReader = ImageReader.newInstance(480, 800, ImageFormat.YUV_420_888, 2)
    private var lastDetectionTask: Task<List<FirebaseVisionFace>>? = null

    private val cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onDisconnected(camera: CameraDevice) {
            showMessage("Camera disconnected.")
        }

        override fun onError(camera: CameraDevice, error: Int) {
            showMessage("Camera error. $error")
            runCamera()
        }

        override fun onOpened(camera: CameraDevice) {
            val surfaces = listOf(svCamera.holder.surface, imageReader.surface)
            camera.createCaptureSession(surfaces, object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession) {

                }

                override fun onConfigured(session: CameraCaptureSession) {
                    val requestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    surfaces.forEach { requestBuilder.addTarget(it) }
                    val request = requestBuilder.build()
                    session.setRepeatingRequest(request, null, null)
                }
            }, null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runCamera()
        imageReader.setOnImageAvailableListener(this, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imageReader.setOnImageAvailableListener(null, null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (RequestCode.toRequestCode(requestCode)) {
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

    override fun onImageAvailable(reader: ImageReader?) {
        reader?.acquireLatestImage()?.let {
            it.use {
                val image = FirebaseVisionImage.fromMediaImage(it, FirebaseVisionImageMetadata.ROTATION_0)
                if (lastDetectionTask == null) {
                    lastDetectionTask = cameraDetector.detectInImage(image)?.addOnSuccessListener {
                        val holder = svCamera.holder
                        val canvas: Canvas? = holder.lockCanvas()
                        val paint = Paint()
                        paint.color = Color.CYAN
                        it.forEach {
                            canvas?.drawRect(it.boundingBox, paint)
                        }
                        canvas?.let {
                            holder.unlockCanvasAndPost(canvas)
                        }
                        lastDetectionTask = null
                    }
                }
            }
        }
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

}
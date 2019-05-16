package com.valery.coursework.ui.main.detection.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Paint
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import androidx.core.graphics.applyCanvas
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.valery.base.BaseFragment
import com.valery.base.utils.RequestCode
import com.valery.base.utils.extensions.showMessage
import com.valery.coursework.R
import kotlinx.android.synthetic.main.fragment_camera_detection.*

class CameraDetectionFragment :
    BaseFragment<CameraDetectionViewModel>(CameraDetectionViewModel::class.java),
    View.OnClickListener,
    ImageReader.OnImageAvailableListener {

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
    private var cameraDevice: CameraDevice? = null
    private var detectFacesTask: Task<List<FirebaseVisionFace>>? = null
    private var imageReader: ImageReader? = null

    private var cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onDisconnected(camera: CameraDevice) {
            showMessage("Camera disconnected.")
        }

        override fun onError(camera: CameraDevice, error: Int) {
            showMessage("Camera error. $error")
            runCamera()
        }

        override fun onOpened(camera: CameraDevice) {
            showMessage("Camera opened.")
            imageReader = ImageReader.newInstance(480, 640, ImageFormat.JPEG, 1)
            Thread {
                Looper.prepare()
                imageReader?.setOnImageAvailableListener(this@CameraDetectionFragment, Handler(Looper.myLooper()))
            }.run()
            cameraDevice = camera
            configureCamera()
        }
    }

    private var sessionCallback: CameraCaptureSession.StateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            val cameraDevice = cameraDevice ?: return
            val imageReader = imageReader ?: return
            val surface = imageReader.surface
            val requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            requestBuilder.addTarget(surface)
            val request = requestBuilder.build()
            session.setRepeatingRequest(request, null, null)
            showMessage("capture session configured: $session")
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            showMessage("capture session configure failed: $session")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runCamera()
    }

    override fun onPrepareObservers() {
        super.onPrepareObservers()
        viewModel.onFrameChanged.observe(Observer {
            it?.let { bitmap ->
                val holder = svCamera.holder
                synchronized(holder) {
                    val canvas = holder.lockCanvas()
                    canvas.drawBitmap(bitmap, 0f, 0f, Paint())
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        })
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

    override fun onClick(v: View?) {

    }

    override fun onImageAvailable(p0: ImageReader?) {
        val reader = p0 ?: return
        viewModel.processImageReader(reader)
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
            Thread().run {
                cameraManager.openCamera(cameraIdList[1], cameraStateCallback, Handler())
            }
        } catch (e: SecurityException) {
            showMessage("Camera's permissions not allowed.")
        }
    }

    private fun configureCamera() {
        val cameraDevice = cameraDevice ?: return
        val imageReader = imageReader ?: return
        val surface = imageReader.surface
        try {
            cameraDevice.createCaptureSession(listOf(surface), sessionCallback, Handler())
        } catch (e: CameraAccessException) {
            showMessage(e.toString())
        }
    }

    private fun detectStaticImage(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
        cameraDetector
            .detectInImage(FirebaseVisionImage.fromBitmap(bitmap))?.addOnSuccessListener {
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
    }


    companion object {
        fun newInstance(): CameraDetectionFragment = CameraDetectionFragment()

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
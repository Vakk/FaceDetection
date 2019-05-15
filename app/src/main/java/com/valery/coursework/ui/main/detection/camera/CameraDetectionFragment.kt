package com.valery.coursework.ui.main.detection.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.hardware.camera2.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import androidx.core.graphics.applyCanvas
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
    View.OnClickListener, SurfaceHolder.Callback {

    override val layoutId: Int = R.layout.fragment_camera_detection

    private val realTimeOpts by lazy {
        FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()
    }

    private val cameraDetector by lazy {
        FirebaseVision.getInstance().getVisionFaceDetector(realTimeOpts)
    }

    private var cameraManager: CameraManager? = null
    private var cameraDevice: CameraDevice? = null

    private var cameraCaptureSessionCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureBufferLost(
            session: CameraCaptureSession,
            request: CaptureRequest,
            target: Surface,
            frameNumber: Long
        ) {
            super.onCaptureBufferLost(session, request, target, frameNumber)
            showMessage("onCaptureBufferLost")
        }

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)
            showMessage("onCaptureCompleted")
        }

        override fun onCaptureFailed(session: CameraCaptureSession, request: CaptureRequest, failure: CaptureFailure) {
            super.onCaptureFailed(session, request, failure)
            showMessage("onCaptureFailed")
        }

        override fun onCaptureProgressed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            partialResult: CaptureResult
        ) {
            super.onCaptureProgressed(session, request, partialResult)
            showMessage("onCaptureProgressed")
        }

        override fun onCaptureSequenceAborted(session: CameraCaptureSession, sequenceId: Int) {
            super.onCaptureSequenceAborted(session, sequenceId)
            showMessage("onCaptureSequenceAborted")
        }

        override fun onCaptureSequenceCompleted(session: CameraCaptureSession, sequenceId: Int, frameNumber: Long) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber)
            showMessage("onCaptureSequenceAborted")
        }

        override fun onCaptureStarted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            timestamp: Long,
            frameNumber: Long
        ) {
            super.onCaptureStarted(session, request, timestamp, frameNumber)
            showMessage("onCaptureStarted")
        }
    }

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

    private var sessionCallback: CameraCaptureSession.StateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            val cameraDevice = cameraDevice ?: return
            val surface = svCamera.holder.surface
            val requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            requestBuilder.addTarget(surface)
            val request = requestBuilder.build()
            session.setRepeatingRequest(request, cameraCaptureSessionCallback, null)
            showMessage("capture session configured: $session")
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            showMessage("capture session configure failed: $session")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runCamera()
        svCamera.holder.addCallback(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        svCamera.holder.removeCallback(this)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.PICK_IMAGE.code -> {
                data?.data?.let { detectStaticImage(it) }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClick(v: View?) {

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
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
        TODO("Add some show to UI")
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
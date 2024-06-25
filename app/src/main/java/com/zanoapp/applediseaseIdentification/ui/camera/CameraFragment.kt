package com.zanoapp.applediseaseIdentification.ui.camera


import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recording
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.FragmentCameraBinding
import com.zanoapp.applediseaseIdentification.ui.camera.ImageAnalyzer.Companion.DIM_IMG_SIZE_X
import com.zanoapp.applediseaseIdentification.ui.camera.ImageAnalyzer.Companion.DIM_IMG_SIZE_Y
import com.zanoapp.applediseaseIdentification.utils.CONTROL_LIFECYCLE_METHODS
import com.zanoapp.applediseaseIdentification.utils.TRACK_FRAGCAMBITMAP_STATE
import kotlinx.coroutines.Job
import java.util.concurrent.ExecutorService


private const val REQUEST_CODE_PERMISSION = 10
private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)


typealias LumaListener = (luma: Double) -> Unit

class CameraFragment : Fragment(), LifecycleOwner,
    ActivityCompat.OnRequestPermissionsResultCallback {

//        private var imageAnalyzerInstance: ImageAnalyzer? = null


    val viewModel: CameraViewModel by viewModels()

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
//    private lateinit var myBitmap: Bitmap
    private lateinit var imageAnalyzer: ImageAnalyzer
    private var runClassifier = false
    private val job = Job()

    private var camera: Camera? = null

    private lateinit var viewFinder: PreviewView
    private lateinit var preview: Preview

    private var _binding: FragmentCameraBinding? = null
    private val binding
        get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(layoutInflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view as ConstraintLayout
        viewFinder = container.findViewById(R.id.viewFinder)

        Log.i(CONTROL_LIFECYCLE_METHODS, "onCreate: called inside the fragment")
        try {
            imageAnalyzer = ImageAnalyzer()
            runClassifier = true
            Toast.makeText(activity, "Model and labels has been loaded", Toast.LENGTH_SHORT).show()

        } catch (io: Exception) {
            Toast.makeText(activity, "Something went wrong ${io.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkForCameraPermission()
    }

    private fun startCamera() {
        Log.i(CONTROL_LIFECYCLE_METHODS, "startCamera: Called")

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()

            try {
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview)
                preview.setSurfaceProvider(viewFinder.surfaceProvider)
//                myBitmap = viewFinder.bitmap!!
                Log.d(
                    TRACK_FRAGCAMBITMAP_STATE,
                    "startCamera: this is inside the try and the bitmap is ${viewFinder.bitmap}"
                )
            } catch (ex: Exception) {
                showToast("Failed to setup camera preview")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onResume() {
        super.onResume()
        Log.i(CONTROL_LIFECYCLE_METHODS, "onResume: called")

        if (allPermissionsGranted()) {
            startCamera()
            runClassifier = true
            periodicClassify.run()
        }

        Log.d(
            TRACK_FRAGCAMBITMAP_STATE,
            "onResume: is view finder visible :${viewFinder.isVisible} && view finder bitmap state is ${viewFinder.bitmap}"
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.i(CONTROL_LIFECYCLE_METHODS, "onRequestPermissionsResult: ")
        checkForCameraPermission()

        if (requestCode == REQUEST_CODE_PERMISSION) {
            Log.d(
                TRACK_FRAGCAMBITMAP_STATE,
                "onRequestPermissionsResult: ViewFinder bitmap is before startCamera() ${viewFinder.bitmap}"
            )
            if (allPermissionsGranted())
                startCamera()
            Log.d(
                TRACK_FRAGCAMBITMAP_STATE,
                "onRequestPermissionsResult: ViewFinder bitmap is after startCamera() ${viewFinder.bitmap}"
            )
        } else {
            Toast.makeText(
                requireContext(),
                "permission not granted by the user",
                Toast.LENGTH_SHORT
            ).show()
            activity?.finish()
        }
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            Log.i(CONTROL_LIFECYCLE_METHODS, "allPermissionsGranted: called")
            if (ContextCompat.checkSelfPermission(
                    requireContext(), permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun checkForCameraPermission() {
        Log.i(CONTROL_LIFECYCLE_METHODS, "checkForCameraPermission: called")
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSION
            )
        }
    }


    private fun classifyFrame() {

        Log.i(CONTROL_LIFECYCLE_METHODS, "classifyFrame:called ")
        if (activity == null) {
            showToast("Failed to setup camera preview")
            return
        }


//        Log.d(
//            TRACK_FRAGCAMBITMAP_STATE,
//            "MyBitmap byte count ${myBitmap.byteCount} && the source provide is  ${viewFinder.surfaceProvider}"
//        )

        viewFinder.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        val previewChild = viewFinder.getChildAt(0)
        if (previewChild is TextureView) {
//            myBitmap = previewChild.getBitmap(DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y)!!
        }


        val textToShow: String
        if (viewFinder.bitmap == null) {
            showToast("the bitmap is null")
        } else {
//            Log.d(TRACK_FRAGCAMBITMAP_STATE, "classifyFrame: bitmap value is $myBitmap")
//            textToShow = imageAnalyzer.classifyFrame(myBitmap)
//            myBitmap.recycle()
//            showToast(textToShow)
        }
    }

    private fun showToast(text: String) {
        val activity: Activity? = activity
        activity?.runOnUiThread { binding.predictionText.text = text }
    }

    private val periodicClassify: Runnable =
        Runnable {
            if (runClassifier) {
                classifyFrame()
            }
        }


    override fun onDetach() {
        super.onDetach()
        Log.i(CONTROL_LIFECYCLE_METHODS, "onDetach: ")
        runClassifier = false
        job.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(CONTROL_LIFECYCLE_METHODS, "onDestroyView: ")
        _binding = null

    }
}


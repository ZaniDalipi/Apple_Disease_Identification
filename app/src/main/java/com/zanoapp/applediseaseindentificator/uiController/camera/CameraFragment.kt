package com.zanoapp.applediseaseindentificator.uiController.camera


import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Bundle
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.zanoapp.applediseaseindentificator.databinding.FragmentCameraBinding
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


//arbitrary value to track the permission request
private const val REQUEST_CODE_PERMISSION = 10
private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)

class CameraFragment : Fragment(), LifecycleOwner , ActivityCompat.OnRequestPermissionsResultCallback{

    private lateinit var viewModel: CameraViewModel
    private var checkedPermissions = false
    private var runClassifier: Boolean = true
    private val job = Job()
    private val uiCoroutineScope = CoroutineScope(Dispatchers.Main + job)
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView

   // private lateinit var bitmap:Bitmap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentCameraBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        viewFinder = binding.viewFinderCameraX

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()){
            viewFinder.post { startCamera() }
        }else{
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION)
        }
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //val cameraViewModelFactory = CameraViewModelFactory()
       // viewModel = ViewModelProvider(this,cameraViewModelFactory).get(CameraViewModel::class.java)
    }


    private fun startCamera(){

        if(!checkedPermissions && !allPermissionsGranted()){
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION)
        }else{
            viewFinder.post{ startCamera() }
            checkedPermissions = true
        }

        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(640, 480))
            setLensFacing(CameraX.LensFacing.BACK)
        }.build()
//        build the viewfinder use case
        val preview = Preview(previewConfig)

        uiCoroutineScope.launch {
            //        code that will replace the layout everytime that the layout is recomputed
            preview.setOnPreviewOutputUpdateListener {
                //            remove and re add it (surfacetexureview)
                val parent = viewFinder.parent as ViewGroup
                parent.removeView(viewFinder)
                parent.addView(viewFinder, 1)

                viewFinder.surfaceTexture = it.surfaceTexture
                updateTransform()
            }
        }
        preview.removePreviewOutputListener()


        val imageAnalysisConfig = ImageAnalysisConfig.Builder().apply {
        setTargetResolution(Size(viewFinder.width, viewFinder.height))
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        val imageAnalysisUseCase = ImageAnalysis(imageAnalysisConfig).apply {


        }

        CameraX.bindToLifecycle(this, preview, imageAnalysisUseCase)

    }
    // viewfinder transformations that will make sure rotations work well
    private fun updateTransform(){
        val matrix = Matrix()

        val centerX =  viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        val rotateDegrees = when(viewFinder.display.rotation){
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }

        matrix.postRotate(-rotateDegrees.toFloat(), centerX, centerY)

//        apply transformation to textureview
        viewFinder.setTransform(matrix)


    }
 /**
    CAMERA PERMISSION
*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        checkForCameraPermission()

        if (requestCode == REQUEST_CODE_PERMISSION){
            if (allPermissionsGranted())
                viewFinder.post {startCamera()}
        }else{
            Toast.makeText(requireContext(), "permission not granted by the user", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    context!!, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun checkForCameraPermission(){
        if (allPermissionsGranted()){
            viewFinder.post{ startCamera() }
        }else{
            ActivityCompat.requestPermissions(this.requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION)
        }
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
           // updateTransform()
        }
    }

    private fun showToast(text: List<String>) {
        val activity: Activity? = activity
        activity?.runOnUiThread { predictionText.text = text.component1()}
    }

  /*  fun classifyFrame(){

            bitmap = viewFinder.getBitmap(bitmap)
            val textToShow =  classificationFragment.classifyframe(bitmap)
            bitmap.recycle()
            showToast(textToShow)

    }*/


   /* private fun startClassifier(){
        uiCoroutineScope.launch {
            if (runClassifier) {
                classifyFrame()
            }
        }
    }*/

    override fun onDetach() {
        super.onDetach()
        runClassifier = false
        job.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}

package com.zanoapp.applediseaseindentificator.uiController.camera

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.*
import com.zanoapp.applediseaseindentificator.uiController.classification.ClassificationFragment
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit

class CameraViewModel: ViewModel(), ImageAnalysis.Analyzer{

    private val job = Job()

    private val uiCoroutineScope = CoroutineScope(Dispatchers.Main + job)
    private val defaultScope = CoroutineScope(Dispatchers.Default + job)
    var remoteModel = FirebaseCustomRemoteModel.Builder(MODEL_PATH_REMOTE).build()
    private val localModel = FirebaseCustomLocalModel.Builder().setAssetFilePath(localModelName).build()
    private var imgData: ByteBuffer
    private val interpreterInstance: FirebaseModelInterpreter
        get() = _interpreter.value!!

    private val interpreterOptions
        get() =_interpreterOptions.value

    //private val labelList = MutableLiveData<List<String>>()


    private val _bitmapDataFromCamera = MutableLiveData<Bitmap>()
    val bitmapdataFromCamera: LiveData<Bitmap>
            get() = TODO("imageProxyToBitmapConverter() invoke method here so that we can pass through to convert it to bytebuffer")



    private val _interpreter = MutableLiveData<FirebaseModelInterpreter>()
    private val _interpreterOptions = MutableLiveData<FirebaseModelInterpreterOptions>()

    init {
        remoteModel
        localModel
        downloadRemoteModel()
    }
    private fun downloadRemoteModel() {
        uiCoroutineScope.launch {
            remoteModel = getRemoteModelInBackground()
        }
    }

    private suspend fun getRemoteModelInBackground(): FirebaseCustomRemoteModel {

        return suspendCancellableCoroutine { cont ->
            defaultScope.launch {
                Log.d("$TAG👍👍👍", "Downloading model ...")
            }

            var conditionsBuilder: FirebaseModelDownloadConditions.Builder =
                FirebaseModelDownloadConditions.Builder()
                    .requireWifi()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                conditionsBuilder = conditionsBuilder
                    .requireCharging()
                    .requireDeviceIdle()

            }
            val conditions = conditionsBuilder.build()
            val manager = FirebaseModelManager.getInstance().download(remoteModel, conditions)

            manager.addOnFailureListener {
                    defaultScope.launch {
                        getRemoteModelInBackground()
                        Log.d("$TAG 😒😒😒 ", "Please let it download it is a better version of model ")
                    }
                }
            manager.addOnCompleteListener {
                    if (!it.isSuccessful){
                        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                        Log.i("$TAG 😒😒😒",  "Please let us download the model for better experience , model name: ${remoteModel.modelName}")
                    }

                    val msg = "Remote model successfully downloaded"
                    uiCoroutineScope.launch {
                        Log.i("$TAG 😎😎😎", remoteModel.modelName!!)
                    }
                    Log.d("model", msg)
                }
        }
    }

    private fun checkIfModelIsDownloaded(localModel: FirebaseCustomLocalModel,
                                         remoteModel: FirebaseCustomRemoteModel){
        uiCoroutineScope.launch {
            FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
                .addOnSuccessListener{ isDownloaded ->
                     _interpreterOptions.value =
                        if (isDownloaded){
                            FirebaseModelInterpreterOptions.Builder(remoteModel).build()
                        }else{
                            FirebaseModelInterpreterOptions.Builder(localModel).build()
                        }
                    _interpreter.value = FirebaseModelInterpreter.getInstance(_interpreterOptions.value!!)
            }
        }
    }

    @Throws(FirebaseMLException::class)
    private fun createInterpreter(localModel: FirebaseCustomLocalModel): FirebaseModelInterpreter?{

        _interpreterOptions.value = FirebaseModelInterpreterOptions.Builder(localModel).build()
        _interpreter.value = interpreterOptions?.let { FirebaseModelInterpreter.getInstance(it) }
        Log.d(ClassificationFragment.TAG, "Local model interpreter initialized")
        return interpreterInstance
    }

    private fun createInputOutputOptions(): FirebaseModelInputOutputOptions{

        val input = arrayOf(DIM_BATCH_SIZE,DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, DIM_PIXEL_SIZE)
        val outputs = arrayOf(DIM_BATCH_SIZE, labelList.size)

        return FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(0, FirebaseModelDataType.FLOAT32, input.toIntArray())
            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, outputs.toIntArray())
            .build()
    }

     /*fun convertBitmapToByteByffer(bitmap: Bitmap): ByteBuffer {

         if (imgData == null){
             return
         }
         *//*re reading the buffer and cleaning the old one *//*
         imgData.rewind()

         bitmap.getPixels(intValues, 0, 0 ,0,0, bitmap.width, bitmap.height)

         val pixel = 0
         val startTime = SystemClock.uptimeMillis()

         for(widthReader in  0 until  DIM_IMG_SIZE_X){
             for (heightReader in 0 until DIM_IMG_SIZE_Y){
                 val valuesHolder = intValues[pixel.plus(1)]
                 imgData.putFloat((((valuesHolder shr 16 and 0xFF) - IMAGE_MEAN)/ IMAGE_STRIDE))
                 imgData.putFloat((((valuesHolder shr 8 and 0xFF) - IMAGE_MEAN)/ IMAGE_STRIDE))
                 imgData.putFloat((((valuesHolder  and 0xFF) - IMAGE_MEAN)/ IMAGE_STRIDE))
             }
         }
         val endTime = SystemClock.uptimeMillis()

     }
*/

     private fun bitmapToInputArray(): Array<Array<Array<FloatArray>>> {
         // [START mlkit_bitmap_input]
         val bitmap = Bitmap.createScaledBitmap(_bitmapDataFromCamera.value!!, 224, 224, true)

         val batchNum = 0
         val input = Array(1) { Array(224) { Array(224) { FloatArray(3) } } }
         for (x in 0..223) {
             for (y in 0..223) {
                 val pixel = bitmap.getPixel(x, y)
                 // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                 // model. For example, some models might require values to be normalized
                 // to the range [0.0, 1.0] instead.
                 input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 255.0f
                 input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 255.0f
                 input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 255.0f
             }
         }
         // [END mlkit_bitmap_input]
         return input
     }

    /*fun classifyFrame(bitmap: Bitmap): List<String>{

        var topLabels = mutableListOf<String>()

        _interpreter.value?.let {
            val localModel = FirebaseCustomLocalModel.Builder().build()
            _interpreter.value = createInterpreter(localModel)
            val input = bitmapToInputArray()
            val inputOutputOptions = createInputOutputOptions()

            val inputs = FirebaseModelInputs.Builder()
                .add(input)
                .build()

            Log.i("$TAG 🙄🙄🙄",  _interpreter.value.toString() )

             _interpreter.value?.run(inputs, inputOutputOptions)
                 ?.addOnSuccessListener {
                     val labelsReader = BufferedReader(InputStreamReader(Application().resources.assets.open(LABEL_PATH)))


                 }

            val startTime = SystemClock.uptimeMillis()

        }
        return topLabels
    }*/

    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {

        var lastAnalyzedTimestamp = 0L
        val localModel = FirebaseCustomLocalModel.Builder().build()
        _interpreter.value = createInterpreter(localModel)
        val inputOutputOptions = createInputOutputOptions()



        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >=
            TimeUnit.SECONDS.toMillis(1)) {
            lastAnalyzedTimestamp = currentTimestamp
        }

        /*The ImageProxy object sent into the analyze() method contains information about our latest image in YUV format.
        This means the image is broken into three planes: the first is a measure of brightness, and the second and third are measures of color in the red and blue space.
        We can retrieve these three planes like so*/
        val y = image?.planes?.get(0)
        val u = image?.planes?.get(1)
        val v = image?.planes?.get(2)

        /*get the remining buffers from each plane*/
        val numOfPixelsInY = y?.buffer?.remaining()
        val numOfPixelsInU = u?.buffer?.remaining()
        val numOfPixelsInV = v?.buffer?.remaining()

        /*convert them into a singe YUV formatted ByteArray*/
        val imageDataYUV = ByteArray(numOfPixelsInY!! + numOfPixelsInU!! + numOfPixelsInV!!)

        y.buffer.get(imageDataYUV, 0, numOfPixelsInY)
        u.buffer.get(imageDataYUV, 0 , numOfPixelsInU)
        v.buffer.get(imageDataYUV, 0 , numOfPixelsInV)

        val inputs = FirebaseModelInputs.Builder()
            .add(imageDataYUV)
            .build()

        Log.i("$TAG 🙄🙄🙄",  _interpreter.value.toString() )

        _interpreter.value?.run(inputs, inputOutputOptions)
            ?.addOnSuccessListener {
                val labelsReader = BufferedReader(InputStreamReader(Application().resources.assets.open(LABEL_PATH)))

                for(i in labelsReader.lineSequence()){
                    val label = labelsReader.readLine()
                    labelList.add(label)
                    Log.i("$TAG: output" , String.format("%s: %1.4f", label, inputOutputOptions.zzpa()))
                }

                labelsReader.close()
            }
    }

/*
    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {


        val imageData =



          val localModel = FirebaseCustomLocalModel.Builder().build()
          val inputOutputOptions = createInputOutputOptions()
          var topLabels: List<String> = labelList.dropLast(9)
          val inputs = toByteArray(imgData.int)

          if (image?.image != null){

              val imageSize = Size(image.width, image.height)
              val data = imgData.toByteArray()


    }*/

    private val labelList by lazy {
        BufferedReader(InputStreamReader(Application().resources.assets.open(ClassificationFragment.LABEL_PATH))).lineSequence().toMutableList()
    }

    init {

        // allocate a to the variable or to a bytebuffer directly there is start and the limit or the capacity in the parameters
        imgData = ByteBuffer.allocateDirect(
            4 * DIM_BATCH_SIZE *
                    DIM_IMG_SIZE_X *
                    DIM_IMG_SIZE_Y *
                    DIM_PIXEL_SIZE
        )
        imgData.order(ByteOrder.nativeOrder())
    }

    companion object {

        val localModelName = Application().resources.assets.list("")?.firstOrNull { it.endsWith(".tflite" ) || it.endsWith(".lite") }
            ?: throw(RuntimeException("Don't forget to add the tflite file to your assets folder"))
        const val MODEL_PATH_REMOTE = "Plant_Disease_Final_Version"
        const val TAG = "ModelState: "
        const val LABEL_PATH = "retrained_labels.txt"
        const val DIM_BATCH_SIZE = 1
        const val DIM_PIXEL_SIZE = 3
        const val DIM_IMG_SIZE_X = 224
        const val DIM_IMG_SIZE_Y = 224
        const val IMAGE_MEAN = 128
        const val IMAGE_STRIDE = 128.0f
        const val RESULTS_TO_SHOW = 3
        private const val FILTER_STAGES = 3
        private const val FILTER_FACTOR = 0.4f
        //Preallocated buffers for storing image data in
        //Preallocated buffers for storing image data in
        val intValues = intArrayOf(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }


}
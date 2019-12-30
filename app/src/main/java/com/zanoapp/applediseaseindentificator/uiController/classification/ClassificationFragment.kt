package com.zanoapp.applediseaseindentificator.uiController.classification

/**
 * 1. in this codebase we are downloading a remote and a local model that is added under assets directory.
 * 2. to get those models we use FirebaseCustomRemote/LocalModel builder to build it later in that field when activity is created
 * 3. after that we download the latest model that has been added in firebaseRemote Ml model
 * 4. in downloadModelFromFirebase() we do that one where we define the requirements for when to download that model and we set listeners that will give us info if the model has been downloaded
 * 5. checkIfModelIsDownloaded() it is a function that will check if a model is downloaded than use the remote one else use the local one , this step may be done differently if we dont want to
 * add the model locally to make some part of code gray or disabled and to show a dialog of downloading content
 *
 */

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.*
import com.zanoapp.applediseaseindentificator.uiController.camera.CameraViewModelFactory
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.Comparator
import kotlin.coroutines.resumeWithException
import kotlin.experimental.and


class ClassificationFragment : Fragment() {

    private lateinit var remoteModel: FirebaseCustomRemoteModel
    private lateinit var localModel: FirebaseCustomLocalModel
    private lateinit var firebaseInterpreter: FirebaseModelInterpreter
    private lateinit var coroutineUiScope: CoroutineScope
    private lateinit var imageData: ByteBuffer

    private val imageBuffer = IntArray(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y)


    private val labelList by lazy {
        BufferedReader(InputStreamReader(resources.assets.open(LABEL_PATH))).lineSequence().toList()
    }
    /*private lateinit var mLabelList: List<String>
    private lateinit var labelProbArray: Array<FloatArray>
    private lateinit var filterLabelProbArray: Array<FloatArray>*/


    companion object {

        const val TAG = "ModelState✔✔✔✔"
        const val LABEL_PATH = "retrained_labels.txt"
        const val DIM_BATCH_SIZE = 1
        const val DIM_PIXEL_SIZE = 3
        const val DIM_IMG_SIZE_X = 224
        const val DIM_IMG_SIZE_Y = 224
        const val RESULTS_TO_SHOW = 3
        private const val FILTER_STAGES = 3
        private const val FILTER_FACTOR = 0.4f
        var mLabelList: Map<String, Float>? = null
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val job = Job()
        this.coroutineUiScope = CoroutineScope(Dispatchers.Main + job)
      /*   labelProbArray = Array(1) { FloatArray(mLabelList.size) }
          filterLabelProbArray = Array(FILTER_STAGES) { FloatArray(mLabelList.size) }*/


        val viewModel = ViewModelProvider(this).get(ClassificationViewModel::class.java)

        viewModel.modelStateDownloaded.observe(viewLifecycleOwner , androidx.lifecycle.Observer<Boolean>{
            if (it){
                Toast.makeText(context, "New version of model has been downloaded",Toast.LENGTH_SHORT).show()
                viewModel.doneDownloading()
            }
        })

    }

    private fun configureRemoteModelSource(){
        remoteModel = FirebaseCustomRemoteModel.Builder("plant_disease_v1").build()
    }


// checking if the model is in the assets file and we are loading it in here than asign to the field localModel that can be accesed in this scope
    private fun configureLocalModelSource(){
        val localModelName = resources.assets.list("plant_disease_v1.tflite")?.firstOrNull { it.endsWith(".tflite") }
            ?: throw(RuntimeException("Dont forget to add the tflite file to your assets folder"))
        Log.d(TAG, "Local Model found: $localModelName")

        localModel = FirebaseCustomLocalModel.Builder().setAssetFilePath("plant_disease_v1.tflite").build()
    }

    private suspend fun downloadModelFromFirebase(remoteModel: FirebaseCustomRemoteModel) {

        return suspendCancellableCoroutine { cont ->
            coroutineUiScope.launch {
                Toast.makeText(context, "Downloading model ...", Toast.LENGTH_SHORT).show()
                Log.d("model", "Downloading model ...")
            }

            val conditions = FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build()

            FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnCompleteListener {
                    if (!it.isSuccessful) cont.resumeWithException(
                        RuntimeException("Remote model failed to download", it.exception)
                    )

                    val msg = "Remote model successfully downloaded"
                    coroutineUiScope.launch { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                    Log.d("model", msg)
                }
        }
    }

// creating an interpreter from the local model witch will be replaced everytime we add a new remote model to the firebase
    @Throws(FirebaseMLException::class)
    private fun createInterpreter(localModel: FirebaseCustomLocalModel): FirebaseModelInterpreter?{

        val options = FirebaseModelInterpreterOptions.Builder(localModel).build()
        firebaseInterpreter = FirebaseModelInterpreter.getInstance(options)!!
        Log.d(TAG, "Local model interpreter initialized")
        return firebaseInterpreter
    }

    /**
     * here we are checking if the model is downloaded if not than we are using the local one
     */
    private fun checkIfModelIsDownloaded(remoteModel: FirebaseCustomRemoteModel, localModel: FirebaseCustomLocalModel){
        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel).addOnSuccessListener {isDownloaded ->
            val modelInterpreterOptions =
                if (isDownloaded){
                    FirebaseModelInterpreterOptions.Builder(remoteModel).build()
                }else{
                    FirebaseModelInterpreterOptions.Builder(localModel).build()
                }
            val interpreter = FirebaseModelInterpreter.getInstance(modelInterpreterOptions)
        }
    }

    fun addDownloadListener(
        remoteModel: FirebaseCustomRemoteModel,
        conditions: FirebaseModelDownloadConditions
    ){
        FirebaseModelManager.getInstance().download(remoteModel, conditions).addOnCompleteListener{
            // Download complete. Depending on your app, you could enable the ML
            // feature, or switch from the local model to the remote model, etc.
        }
    }

    @Throws(FirebaseMLException::class)
    private fun defineInputAndOutputShape() : FirebaseModelInputOutputOptions {

        val inputDims = arrayOf(DIM_BATCH_SIZE, DIM_IMG_SIZE_Y, DIM_IMG_SIZE_Y, DIM_PIXEL_SIZE)
        val outputDims = arrayOf(DIM_BATCH_SIZE, labelList.size)

         return FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(0, FirebaseModelDataType.FLOAT32, inputDims.toIntArray())
            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, outputDims.toIntArray())
            .build()
    }

    /**
     * transformation of bitmap to format that is is accessible for the model and preallocating imageData
     */

    private fun bitmapToInputArray(bitmap: Bitmap): ByteBuffer {

        imageData = ByteBuffer.allocateDirect(
            4 *  DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE
        ).apply {
            order(ByteOrder.nativeOrder())
            rewind()
        }


        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, true)

        scaledBitmap.getPixels(
            imageBuffer, 0, scaledBitmap.width, 0 , 0 , scaledBitmap.width, scaledBitmap.height)

        /*convert image into int points */
        var pixel = 0
        for (i in 0 until DIM_IMG_SIZE_X){
            for (j in 0 until DIM_IMG_SIZE_Y ){
                val `val` = imageBuffer[pixel++]
                imageData.put((`val` shr 16 and 0xFF).toByte())
                imageData.put((`val` shr 8 and 0xFF).toByte())
                imageData.put((`val` and 0xFF).toByte())

        /*// define the input where we have to define 3 dimensional array this in python would be array[1, 224, 224, 3]
        val imageData =
            Array(1) {Array(224) {Array(224){FloatArray(3)} } }
        // iterate for x cordinate and y cordinate so we go pixel by pixel
        for(x in 0..223) {
            for (y in 0..223) {
                //calculate the total pixels checked in one picture
                val pixel = bitmap.getPixel(x, y)

                *//*normalization to the channels from [-1.0 to 1.0] also read all the rgb colors and get that bitmap through the neural network by doing convolution*//*
                imageData[batchNum][x][y][0] = (Color.red(pixel) - 127) / 255.0f
                imageData[batchNum][x][y][1] = (Color.green(pixel) - 127) / 255.0f
                imageData[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 255.0f
*/
            }
        }
        return imageData
    }

    /*here is where we classify the input */
    fun classifyframe(bitmap: Bitmap): List<String>{

        val localModel = FirebaseCustomLocalModel.Builder().build()
        val firebaseInterpreter = createInterpreter(localModel)
        imageData = bitmapToInputArray(bitmap)
        val inputOutputOptions = defineInputAndOutputShape()
        var topLabels: List<String> = emptyList()


            val modelInputs = FirebaseModelInputs.Builder()
                .add(imageData)
                .build()

            firebaseInterpreter?.run(modelInputs, inputOutputOptions)?.continueWith {
                val inferenceOutput = it.result?.getOutput<Array<ByteArray>>(0)!!
                topLabels = printTopKLabels(inferenceOutput)
                predictionText.clearComposingText()
                predictionText.addTextChangedListener {
                    predictionText.text = topLabels.size.toString()
                }
        }
        return topLabels
    }

    /**
     * label filtering and setup */

    private val sortedLabels = PriorityQueue(
        RESULTS_TO_SHOW,  // the initialCapacity
// next we setup a comparator that will used to order this priority queue
        Comparator { o1: Map.Entry<String?, Float>, o2: Map.Entry<String?, Float>
            -> o1.value.compareTo(o2.value) })

    data class LabelConfidence(val label: String, val confidence: Float)

    fun printTopKLabels(inferenceOutput: Array<ByteArray>) : List<String>{

        val imageInference = inferenceOutput.first()

        return labelList.mapIndexed { index, label ->
            LabelConfidence(label, confidence = (imageInference[index] and 0xFF.toByte()) / 255.0f)
        }.sortedBy { it.confidence }.reversed().map { "${it.label} : ${it.confidence}" }
            .subList(0, kotlin.math.min(labelList.size, RESULTS_TO_SHOW))
    }

    private fun userInterfaceResult(probabilities: FloatArray): List<String> {
        val labelList = mutableListOf<String>()

        val reader = BufferedReader(
            InputStreamReader(resources.assets.open("retrained_labels.txt")))

        for(i in probabilities.indices){
            val label = reader.readLine()
                labelList.add(label)
            Log.i("MLKIT" , String.format("%s: %1.4f", label, probabilities[i]))
        }

        reader.close()
        return labelList
    }
}


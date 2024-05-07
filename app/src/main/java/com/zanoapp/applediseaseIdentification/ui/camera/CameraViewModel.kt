package com.zanoapp.applediseaseIdentification.ui.camera

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.zanoapp.applediseaseIdentification.utils.TRACK_MODEL_INFERENCE
import kotlinx.coroutines.*
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val job = Job()

    var _labelList = MutableLiveData(mutableListOf<String>())
    private val labelList: LiveData<MutableList<String>>
        get() = _labelList

    var myLocalModel = Application().assets.open("plant_disease_mode_v1.lite")

    private val uiCoroutineScope = CoroutineScope(Dispatchers.Main + job)
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    /** Variable that will be used to define the size of the image that is passed as input*/
    private var imgData = ByteBuffer.allocateDirect(
        4 * DIM_BATCH_SIZE *
                DIM_IMG_SIZE_X *
                DIM_IMG_SIZE_Y *
                DIM_PIXEL_SIZE
    )

    private val _bitmapDataFromCamera = MutableLiveData<Bitmap>()
    private val _interpreter = MutableLiveData<Interpreter>()


    private fun getRemoteModelInBackground() {

        viewModelScope.launch {

            Log.i("labels", labelList.value?.size.toString())

            val conditions = CustomModelDownloadConditions.Builder()
                .requireWifi()
                .build()

            FirebaseModelDownloader.getInstance()
                .getModel(
                    MODEL_PATH_REMOTE,
                    DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                    conditions
                )
                .addOnSuccessListener {
                    /*TODO("We can inform the user that model is downloading" +
                            "we can switch from local model to remote model")**/

                    Log.i("$TAG_MODEL_STATE👍👍👍", "Downloading the model...")
                    val modelFile = it.file
                    if (modelFile != null) {
                        _interpreter.value = Interpreter(modelFile)
                        Log.i(
                            TAG_MODEL_STATE, "😉😉 current model is: ${modelFile.name} " +
                                    "its space is ${modelFile.absoluteFile}"
                        )
                    } else {

                    }
                }.addOnFailureListener {
                    Log.i(
                        TAG_MODEL_STATE,
                        "Couldn't download the model something went wrong ${it.localizedMessage}"
                    )

                    val localModel = FirebaseLocalModel.Builder(MODEL_PATH_LOCAL)
                        .setAssetFilePath(MODEL_PATH_LOCAL)
                        .build()
                }
        }
    }


    fun runInference(myBitmap: Bitmap) {
        val bitmap = Bitmap.createScaledBitmap(myBitmap, 224, 244, true)

        Log.i(
            TRACK_MODEL_INFERENCE,
            "🧔🧔 the input bitmap width is : ${bitmap.width} and height : ${bitmap.height}" +
                    "and its allocation byte is 👩🏿‍⚖️👩🏿‍⚖ : ${bitmap.allocationByteCount}"
        )

        val input = ByteBuffer.allocateDirect(imgData.capacity()).order(ByteOrder.nativeOrder())
        Log.i(TRACK_MODEL_INFERENCE, "»» the input size is ${input.capacity()}")
//        reading the bitmap and getting the total pixels to pass to the nn
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixels = bitmap.getPixel(x, y)


                val r = Color.red(pixels)
                val g = Color.green(pixels)
                val b = Color.blue(pixels)

                Log.i(
                    TRACK_MODEL_INFERENCE,
                    "🚌 🚌 the size of pixels not normalized ${r.toByte()}"
                )

                // normalize
                val redNormalized = (r - 127) / 255f
                val greenNormalized = (g - 127) / 255f
                val blueNormalized = (b - 127) / 255f
                Log.i(
                    TRACK_MODEL_INFERENCE,
                    "🚓 🚓 the size of pixels normalized for red is ${redNormalized.toBits()}"
                )

                input.also {
                    it.putFloat(redNormalized)
                    it.putFloat(greenNormalized)
                    it.putFloat(blueNormalized)
                }
            }
        }

        val bufferSize = 12 * Float.MAX_VALUE / Byte.MAX_VALUE
        val modelOutput =
            ByteBuffer.allocateDirect(bufferSize.toInt()).order(ByteOrder.nativeOrder())
        _interpreter.value?.run(input, modelOutput)

        Log.i(TRACK_MODEL_INFERENCE, "🛴 the model output is ${modelOutput.asFloatBuffer()} ")

        modelOutput.rewind()

        val probabilities = modelOutput.asFloatBuffer()

        try {
            val reader = BufferedReader(
                InputStreamReader(Application().assets.open("Labels.txt"))
            )

            for (i in probabilities.capacity().toString()) {
                val label = reader.readLine()
                Log.i(TAG_MODEL_STATE, "💕💕💕💕💕 $label")
                val probability = probabilities.get(i.toInt())
                Log.i(
                    TAG_MODEL_STATE, " $💖💖💖💖💖 the probabilities are $probabilities " +
                            "and current probability is $probability " +
                            "the capacity is ${probabilities.capacity()} "
                )
                println("$label: $probability")
            }
        } catch (e: IOException) {
            Log.i(TAG_MODEL_STATE, "no file was found ${e.message}")
        }
    }



        fun readLabels() {
            val listOfLabels = mutableListOf<String>()
            val label = Application().assets.open("Labels.txt", 1).bufferedReader().use {
                it.readText()
            }
            listOfLabels.add(label)
            for (items in listOfLabels) {
                Log.i("myListOfDisease", listOfLabels.toString())
            }

            _labelList.value = listOfLabels
        }

    private fun applyFilterToLabels() {
        val numberOfLabels = labelList.value?.size
        Log.i("myListOfDisease", numberOfLabels.toString())


        /*first stage of filtering */
        for (j in 0 until numberOfLabels!!) {
            filterLabelProbArray!![0][j] += FILTER_FACTOR * (labelProbArray!![0][j] -
                    filterLabelProbArray!![0][j])
        }
        for (i in 1 until FILTER_STAGES) {
            for (j in 0 until numberOfLabels) {
                filterLabelProbArray!![i][j] += FILTER_FACTOR * (
                        filterLabelProbArray!![i - 1][j] - filterLabelProbArray!![i][j]
                        )
            }
        }
        for (j in 0 until numberOfLabels) {
            labelProbArray!![0][j] = filterLabelProbArray!![FILTER_STAGES - 1][j]
        }
    }

    private val sortedLabels =
        PriorityQueue(
            RESULT_TO_SHOW
        )
        // the initialCapacity next we setup a comparator that will used to order this priority queue
        { o1: Map.Entry<String?, Float>, o2: Map.Entry<String?, Float> ->
            o1.value.compareTo(
                o2.value
            )
        }


    private fun printTopKLabels(): String {
        for (i in labelList.value?.indices!!) {
            sortedLabels.add(
                // creating an AbstractMap so we get only the skeleton of the Map Interfece so it can be running faster than Map interface ,
// SimpleEntry () we can build custom maps
                AbstractMap.SimpleEntry(
                    labelList.value!![i],
                    labelProbArray!![0][i]
                )
            )
            if (sortedLabels.size > RESULT_TO_SHOW) {
                sortedLabels.poll()
            }
        }

        var textToShow = ""
        val size = sortedLabels.size
        for (i in 0 until size) {
            val label: Map.Entry<String?, Float>? = sortedLabels.poll()
            textToShow = String.format("\n%s: %4.2f", label!!.key, label.value) + textToShow
        }
        return textToShow
    }

    init {
        Log.i(LABEL_NAME, "${labelList.value?.size}")
        getRemoteModelInBackground()
        //readLabels()
        printTopKLabels()
        applyFilterToLabels()


        // allocate a to the variable or to a bytebuffer directly there is start and the limit or the capacity in the parameters
        imgData.order(ByteOrder.nativeOrder())
        labelProbArray = Array(1) { FloatArray(labelList.value?.size!!) }
        filterLabelProbArray = Array(FILTER_STAGES) { FloatArray(labelList.value?.size!!) }
    }

    companion object {
        const val TAG_MODEL_STATE = "ModelState:"
        const val TAG_INTERPRERER = "lnterpreterState:"
        const val MODEL_PATH_REMOTE = "Plant_Disease_Final_Version"
        const val LABEL_NAME = "Labels.txt"
        const val MODEL_PATH_LOCAL = "plant_disease_model_v1.lite"
        const val DIM_BATCH_SIZE = 1
        const val DIM_PIXEL_SIZE = 3
        const val DIM_IMG_SIZE_X = 224
        const val DIM_IMG_SIZE_Y = 224
        const val RESULT_TO_SHOW = 3
        val intValues = intArrayOf(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y)
        private var labelProbArray: Array<FloatArray>? = null
        private var filterLabelProbArray: Array<FloatArray>? = null
        private const val FILTER_STAGES = 3
        private const val FILTER_FACTOR = 0.4f
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}




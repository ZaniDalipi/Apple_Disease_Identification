package com.zanoapp.applediseaseIdentification.ui.camera

import android.app.Application
import android.graphics.*
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.*
import kotlinx.coroutines.*
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.coroutines.resume


class CameraViewModel : ViewModel()/*, ImageAnalysis.Analyzer*/ {

    private val job = Job()

    private val uiCoroutineScope = CoroutineScope(Dispatchers.Main + job)
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private var remoteModel = FirebaseCustomRemoteModel.Builder(MODEL_PATH_REMOTE).build()
    private var imgData: ByteBuffer
    private val imageProxy: ImageProxy? = null
    private val interpreterOptions
        get() = _interpreterOptions.value

    private val localModel =
        FirebaseCustomLocalModel.Builder().setFilePath(MODEL_PATH_LOCAL).build()

    //private val labelList = MutableLiveData<List<String>>()
    private val _bitmapDataFromCamera = MutableLiveData<Bitmap>()
    val bitmapdataFromCamera: Bitmap
        get() = convertImageProxyToBitmap(imageProxy!!)
    //TODO("imageProxyToBitmapConverter() invoke method here so that we can pass through to convert it to bytebuffer")


    private val _interpreter = MutableLiveData<FirebaseModelInterpreter>()
    private val _interpreterOptions = MutableLiveData<FirebaseModelInterpreterOptions>()

    init {
//        loadLabels()
        remoteModel
        localModel
        downloadRemoteModel()
    }

    private fun downloadRemoteModel() {

        uiCoroutineScope.launch {
            _interpreter.value = getRemoteModelInBackground(remoteModel)
            Log.i("$TAG_MODEL_STATE😈😈😈1", _interpreter.value.toString())
        }
    }

    private suspend fun getRemoteModelInBackground(remoteModel: FirebaseCustomRemoteModel): FirebaseModelInterpreter {

        return suspendCancellableCoroutine { cont ->
            Log.i("$TAG_MODEL_STATE👍👍👍", "Downloading model ...")

            Log.i("labels", labelList?.size.toString())

            val conditions = FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build()

            FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnFailureListener {
                }
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        ioScope.launch {
                            getRemoteModelInBackground(remoteModel)
                            Log.i(
                                "$TAG_MODEL_STATE😒😒😒 ",
                                "Please let it download it is a better version of model "
                            )
                        }
                        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                        Log.i(
                            "$TAG_MODEL_STATE😒😒😒",
                            "Please let us download the model for better experience , model name: ${remoteModel.modelName}"
                        )
                    }

                    val msg = "Remote model successfully downloaded"
                    _interpreter.value = FirebaseModelInterpreter.getInstance(
                        FirebaseModelInterpreterOptions.Builder(remoteModel).build()
                    )!!

                    Log.i(
                        "$TAG_MODEL_STATE😎😎😎",
                        "${remoteModel.modelName.toString()}. text: $msg "
                    )
                    Log.i(
                        "$TAG_MODEL_STATE interpreter",
                        "${_interpreter.value.toString().reader()}. text: $msg "
                    )

                    val remoteInterpreter = FirebaseModelInterpreter.getInstance(
                        FirebaseModelInterpreterOptions.Builder(remoteModel).build()
                    )!!

                    Log.i("$TAG_MODEL_STATE🤩🤩🤩", "Remote model interpreter initialized")

                    // Return the interpreter via continuation object
                    cont.resume(remoteInterpreter)
                }
        }
    }

    private fun checkIfModelIsDownloaded() {
        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
            .addOnSuccessListener { isDownloaded ->
                _interpreterOptions.value =
                    if (isDownloaded) {
                        FirebaseModelInterpreterOptions.Builder(remoteModel).build()
                    } else {
                        FirebaseModelInterpreterOptions.Builder(localModel).build()
                    }
                _interpreter.value =
                    FirebaseModelInterpreter.getInstance(_interpreterOptions.value!!)
            }
        Log.i("$TAG_MODEL_STATE checkIfDown", _interpreter.value.toString())
        Log.i("$TAG_MODEL_STATE  optionsVal", _interpreterOptions.value.toString())
    }

    @Throws(FirebaseMLException::class)
    private fun createInterpreter(): FirebaseModelInterpreter? {
        uiCoroutineScope.launch {
            _interpreterOptions.value = FirebaseModelInterpreterOptions.Builder(localModel).build()
            _interpreter.value =
                interpreterOptions?.let { FirebaseModelInterpreter.getInstance(it) }
            Log.i("$TAG_INTERPRERER🤩🤩🤩", "Local model interpreter initialized")
            Log.i(
                "$TAG_INTERPRERER🤩🤩🤩",
                "interpreter options state: ${_interpreterOptions.value}"
            )
            Log.i("$TAG_INTERPRERER🤩🤩🤩", "interpreter object state: ${_interpreter.value}")
        }
        return _interpreter.value
    }


    private fun createInputOutputOptions(): FirebaseModelInputOutputOptions {

        val input = arrayOf(DIM_BATCH_SIZE, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, DIM_PIXEL_SIZE)
        val outputs = arrayOf(DIM_BATCH_SIZE, 12)

        return FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(0, FirebaseModelDataType.FLOAT32, input.toIntArray())
            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, outputs.toIntArray())
            .build()
    }

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

    fun convertImageProxyToBitmap(image: ImageProxy): Bitmap {

        val yBuffer = image.planes[0].buffer // Y
        val uBuffer = image.planes[1].buffer // U
        val vBuffer = image.planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    fun classifyFrame(bitmap: Bitmap): List<String> {

        val topLabels = mutableListOf<String>()

        _interpreter.value?.let {
            _interpreter.value = createInterpreter()
            val input = bitmapToInputArray()
            val inputOutputOptions = createInputOutputOptions()
            val labelList: ArrayList<String>? = null

            val inputs = FirebaseModelInputs.Builder()
                .add(input)
                .build()

            Log.i("$TAG_MODEL_STATE🙄🙄🙄", _interpreter.value.toString())

            _interpreter.value?.run(inputs, inputOutputOptions)
                ?.addOnSuccessListener {
                    try {
                        BufferedReader(
                            InputStreamReader(Application().resources.assets.open(LABEL_NAME))
                        ).use { reader ->
                            var line = reader.readLine()
                            while (line != null) {
                                labelList?.add(line)
                                line = reader.readLine()
                            }
                        }
                    } catch (e: IOException) {
                        Log.e("$TAG_MODEL_STATE😥😥😥", "couldnt read the labels", e)
                    }
                }
            val startTime = SystemClock.uptimeMillis()
        }
        return topLabels
    }

    private fun applyFilterToLabels() {
        val numberOfLabels = labelList?.size

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

/*
    private fun loadLabels(): ArrayList<String>? {
        try {
            val activity = Activity()
            val file = File("retrained_labels.txt")

            val reader = BufferedReader(
                InputStreamReader(activity.resources.assets.open("retrained_labels"))
            ).use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    (labelList as ArrayList<String>).add(line)
                    line = reader.readLine()
                }
            }
        }catch (e: IOException){
            Log.e("$TAG_MODEL_STATE😥😥😥", "couldnt read the labels", e)
        }
        return labelList as ArrayList<String>
    }
*/

    private val sortedLabels =
        PriorityQueue(
            RESULT_TO_SHOW,  // the initialCapacity
// next we setup a comparator that will used to order this priority queue
            Comparator { o1: Map.Entry<String?, Float>, o2: Map.Entry<String?, Float> ->
                o1.value.compareTo(
                    o2.value
                )
            }
        )


    fun printTopKLabels(): String {
        for (i in labelList?.indices!!) {
            sortedLabels.add(
                // creating an AbstractMap so we get only the skeleton of the Map Interfece so it can be running faster than Map interface ,
// SimpleEntry () we can build custom maps
                AbstractMap.SimpleEntry(
                    labelList!![i],
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

    private var labelList: List<String>? = null

    init {
        // allocate a to the variable or to a bytebuffer directly there is start and the limit or the capacity in the parameters
        imgData = ByteBuffer.allocateDirect(
            4 * DIM_BATCH_SIZE *
                    DIM_IMG_SIZE_X *
                    DIM_IMG_SIZE_Y *
                    DIM_PIXEL_SIZE
        )
        imgData.order(ByteOrder.nativeOrder())
        labelProbArray = Array(1) { FloatArray(labelList!!.size) }
        filterLabelProbArray = Array(FILTER_STAGES) { FloatArray(labelList!!.size) }
    }

    companion object {
        const val TAG_MODEL_STATE = "ModelState:"
        const val TAG_INTERPRERER = "lnterpreterState:"
        const val MODEL_PATH_REMOTE = "plant_disease_model_v1"
        const val LABEL_NAME = "retrained_labels.txt"
        const val MODEL_PATH_LOCAL = "plant_disease_model_v1.lite"
        const val DIM_BATCH_SIZE = 1
        const val DIM_PIXEL_SIZE = 3
        const val DIM_IMG_SIZE_X = 299
        const val DIM_IMG_SIZE_Y = 299
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

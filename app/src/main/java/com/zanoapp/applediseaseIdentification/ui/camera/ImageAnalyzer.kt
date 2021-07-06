package com.zanoapp.applediseaseIdentification.ui.camera

import android.app.Activity
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.zanoapp.applediseaseIdentification.utils.CONTROL_LIFECYCLE_METHODS
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*


class ImageAnalyzer constructor(activity: Activity) {

    private val intValues = IntArray(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y)

    private var tfliteModel
            : MappedByteBuffer?
    private var tflite
            : Interpreter?
    private val labelList
            : List<String>
    private val imgData
            : ByteBuffer?


    private val labelProbArray: Array<FloatArray>
    private val filterLabelProbArray: Array<FloatArray>


    private val sortedLabels = PriorityQueue(
        RESULTS_TO_SHOW
    )
    { o1: Map.Entry<String?, Float>, o2: Map.Entry<String?, Float> ->
        o1.value.compareTo(o2.value)
    }

    /** Classifies a frame from the preview stream.  */
    fun classifyFrame(bitmap: Bitmap): String {
        Log.i(CONTROL_LIFECYCLE_METHODS, "classifyFrame: ImageAnalyzer called")
        if (tflite == null) {
            Log.e(TAG, "Image classifier has not been initialized; Skipped.")
            return "Uninitialized Classifier."
        }
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        convertBitmapToByteBuffer(bitmap)
        Log.i(TAG, "classifyFrame:（⊙ｏ⊙） this is a point of obervation of the 2 variables one is the label probablitity witch is ${labelProbArray.size} \n\n and the labels are ${labelList.size}" +
                "the filtered label list is ${filterLabelProbArray.size} ,\n the bitmap is  $bitmap")



        val startTime = SystemClock.uptimeMillis()
        tflite!!.run(imgData, labelProbArray)
        val endTime = SystemClock.uptimeMillis()
        applyFilter()
        var textToShow = printTopKLabels()
        textToShow = (endTime - startTime).toString() + "ms" + textToShow
        return textToShow
    }

    private fun applyFilter() {
        Log.i(CONTROL_LIFECYCLE_METHODS, "applyFilter: ")
        val num_labels = labelList.size


        for (j in 0 until num_labels) {
            filterLabelProbArray[0][j] += FILTER_FACTOR * (labelProbArray[0][j] -
                    filterLabelProbArray[0][j])
        }

        for (i in 1 until FILTER_STAGES_RGB) {
            for (j in 0 until num_labels) {
                filterLabelProbArray[i][j] += FILTER_FACTOR * (filterLabelProbArray[i - 1][j] -
                        filterLabelProbArray[i][j])
            }
        }

        for (j in 0 until num_labels) {
            labelProbArray[0][j] = filterLabelProbArray[FILTER_STAGES_RGB - 1][j]
        }
    }

    /** Closes tflite to release resources.  */
    fun close() {
        tflite!!.close()
        tflite = null
        tfliteModel = null
    }

    /** Reads label list from Assets.  */
    private fun loadLabelList(activity: Activity): List<String> {
        Log.i(CONTROL_LIFECYCLE_METHODS, "loadLabelList: ")
        val labelList = mutableListOf<String>()
        val reader = BufferedReader(InputStreamReader(activity.assets.open(LABEL_PATH)))
        var line: String = " "
        while (reader.readLine().also { line = it } != null) {
            labelList.add(line)
        }
        reader.close()
        return labelList
    }

    /** Memory-map the model file in Assets.  */
    private fun loadModelFile(activity: Activity): MappedByteBuffer {
        Log.i(CONTROL_LIFECYCLE_METHODS, "loadModelFile: ")
        val fileDescriptor = activity.assets.openFd(MODEL_PATH)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel

        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /** Writes Image data into a `ByteBuffer`.  */
    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        Log.i(CONTROL_LIFECYCLE_METHODS, "convertBitmapToByteBuffer: ")
        if (imgData == null) {
            return
        }
        imgData.rewind()


        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixel = 0
        for (i in 0 until DIM_IMG_SIZE_X) {
            for (j in 0 until DIM_IMG_SIZE_Y) {
                val `val` = intValues[pixel++]
                imgData.putFloat(((`val` shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                imgData.putFloat(((`val` shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                imgData.putFloat(((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }

    }

    /** Prints top-K labels, to be shown in UI as the results.  */
    private fun printTopKLabels(): String {
        Log.i(CONTROL_LIFECYCLE_METHODS, "printTopKLabels: ")
        for (i in labelList.indices) {
            sortedLabels.add(
                AbstractMap.SimpleEntry(labelList[i], labelProbArray[0][i])
            )
            if (sortedLabels.size > RESULTS_TO_SHOW) {
                sortedLabels.poll()
            }
        }
        var textToShow = " "
        val size = sortedLabels.size
        for (i in 0 until size) {
            val label = sortedLabels.poll()
            textToShow = String.format("\n%s: %4.2f", label.key, label.value) + textToShow
        }
        return textToShow
    }

    companion object {

        private const val TAG = "classifier"


        private const val MODEL_PATH = "plant_disease_model_v1.lite"
        private const val LABEL_PATH = "Labels.txt"
        private const val RESULTS_TO_SHOW = 3


        private const val DIM_BATCH_SIZE = 1
        private const val DIM_PIXEL_SIZE = 3
        const val DIM_IMG_SIZE_X = 224
        const val DIM_IMG_SIZE_Y = 224
        private const val IMAGE_MEAN = 128
        private const val IMAGE_STD = 128.0f
        private const val FILTER_STAGES_RGB = 3
        private const val FILTER_FACTOR = 0.4f

        fun newInstance() = ImageAnalyzer(Activity())
    }

    /** Initializes an `ImageClassifier`.  */
    init {
        Log.i(CONTROL_LIFECYCLE_METHODS, "Init called in Image Analyzer: ")

        tfliteModel = loadModelFile(activity)
        tflite = Interpreter(tfliteModel!!, null)
        labelList = loadLabelList(activity)

        imgData = ByteBuffer.allocateDirect(
            4 * DIM_BATCH_SIZE *
                    DIM_IMG_SIZE_X *
                    DIM_IMG_SIZE_Y
                    * DIM_PIXEL_SIZE
        )
        imgData.order(ByteOrder.nativeOrder())
        Log.i(TAG, "the label list size is ${labelList.size}: ")
        labelProbArray = Array(1) { FloatArray(labelList.size) }
        filterLabelProbArray = Array(FILTER_STAGES_RGB) { FloatArray(labelList.size) }

    }


}




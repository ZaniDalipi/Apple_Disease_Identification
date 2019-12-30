package com.zanoapp.applediseaseindentificator.uiController.classification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseCustomLocalModel
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
import com.google.firebase.ml.custom.FirebaseModelInterpreter
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions
import kotlinx.coroutines.*
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.coroutines.resumeWithException

class ClassificationViewModel : ViewModel() {

    companion object {
        const val DIM_BATCH_SIZE = 1
        const val DIM_PIXEL_SIZE = 3
        const val DIM_IMG_SIZE_X = 224
        const val DIM_IMG_SIZE_Y = 224
        const val RESULTS_TO_SHOW = 3
        private const val FILTER_STAGES = 3
        private const val FILTER_FACTOR = 0.4f
    }

    private lateinit var localModel: FirebaseCustomLocalModel
    private lateinit var firebaseInterpreter: FirebaseModelInterpreter
    private lateinit var coroutineUiScope: CoroutineScope
    private lateinit var imageData: ByteBuffer
    val labelList: List<String>? = null
    private val labelProbArray: Array<FloatArray>? = null
    private val filterLabelProbArray: Array<FloatArray>? = null

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)
    val defaultScope = CoroutineScope(Dispatchers.Default + job)
    val remoteModel = FirebaseCustomRemoteModel.Builder("plant_disease_model_v1").build()

    /**
     * START
     *      MUTABLELIVEDATA AND LIVEDATA*/

    private val _modelStateDownload = MutableLiveData<Boolean>()
    val modelStateDownloaded: LiveData<Boolean>
        get() = _modelStateDownload

    private val _interpreter = MutableLiveData<FirebaseModelInterpreter>()
    val interpreter: LiveData<FirebaseModelInterpreter>
        get() = _interpreter

    /**
     * END
     *      MUTABLELIVEDATA AND LIVEDATA
     */

    init {
        configureLocalModelSource()
        val remoteModel = FirebaseCustomRemoteModel.Builder("plant_disease_model_v1").build()
    }

    private fun calculateImageDataDimensions() {
        imageData = ByteBuffer.allocateDirect(
            4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE
        )
        imageData.order(ByteOrder.nativeOrder())
    }

    private fun configureLocalModelSource() {
        localModel = FirebaseCustomLocalModel.Builder()
            .setAssetFilePath("plant_disease_v1.tflite")
            .build()
    }


    private suspend fun downloadModelFromFirebase(remoteModel: FirebaseCustomRemoteModel) {

        return suspendCancellableCoroutine { cont ->
            defaultScope.launch {
                val conditions = FirebaseModelDownloadConditions.Builder()
                    .requireWifi()
                    .build()
                FirebaseModelManager.getInstance().download(remoteModel, conditions)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) cont.resumeWithException(
                            RuntimeException("Remote model failed to download", it.exception)
                        )
                        _modelStateDownload.value = true
                        }
                    }
            }
        }
    fun doneDownloading(){
        _modelStateDownload.value = false
    }

    fun createInterpreter(localModel: FirebaseCustomLocalModel){
        val options = FirebaseModelInterpreterOptions.Builder(localModel).build()
        _interpreter.value = FirebaseModelInterpreter.getInstance(options)
    }

    fun checkIfModelIsDownloaded(remoteModel: FirebaseCustomRemoteModel,
                                 localModel: FirebaseCustomLocalModel){

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

    suspend fun classifyFrame(){

    }
}

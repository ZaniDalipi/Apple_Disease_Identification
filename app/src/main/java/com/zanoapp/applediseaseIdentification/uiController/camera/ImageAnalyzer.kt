/*
package com.zanoapp.applediseaseIdentification.uiController.camera

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.zanoapp.applediseaseIdentification.uiController.classification.ClassificationFragment.Companion.DIM_BATCH_SIZE
import com.zanoapp.applediseaseIdentification.uiController.classification.ClassificationFragment.Companion.DIM_IMG_SIZE_X
import com.zanoapp.applediseaseIdentification.uiController.classification.ClassificationFragment.Companion.DIM_IMG_SIZE_Y
import com.zanoapp.applediseaseIdentification.uiController.classification.ClassificationFragment.Companion.DIM_PIXEL_SIZE
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit

class ImageAnalyzer: ImageAnalysis.Analyzer {

    private var lastAnalyzedTimestamp = 0L
    var imageData = ByteBuffer.allocateDirect(
        4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE
    ).apply {
        order(ByteOrder.nativeOrder())
        rewind()
    }
    var imageBuffer = intArrayOf(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y)

    */
/*private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }
    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
        val currentTimeStamp = System.currentTimeMillis()

        if (currentTimeStamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {

            val data = imageData.toByteArray()

            var pixel = 0
            for (i in 0 until DIM_IMG_SIZE_X) {
                for (j in 0 until DIM_IMG_SIZE_Y) {
                    val `val` = imageBuffer[pixel++]
                    imageData.put((`val` shr 16 and 0xFF).toByte())
                    imageData.put((`val` shr 8 and 0xFF).toByte())
                    imageData.put((`val` and 0xFF).toByte())
                }
            }
            Log.d("CameraXApp", "Average luminosity: $pixel")
            lastAnalyzedTimestamp = currentTimeStamp
        }
    }*//*



    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }
    override fun analyze(image: ImageProxy, rotationDegrees: Int) {
        val currentTimestamp = System.currentTimeMillis()
        // Calculate the average luma no more often than every second
        if (currentTimestamp - lastAnalyzedTimestamp >=
            TimeUnit.SECONDS.toMillis(1)) {
            // Since format in ImageAnalysis is YUV, image.planes[0]
            // contains the Y (luminance) plane
            val buffer = image.planes[0].buffer
            // Extract image data from callback object
            val data = buffer.toByteArray()
            // Convert the data into an array of pixel values
            val pixels = data.map { it.toInt() and 0xFF }
            // Compute average luminance for the image
            val luma = pixels.average()
            // Log the new luma value
            Log.d("CameraXApp", "Average luminosity: $luma")
            // Update timestamp of last analyzed frame
            lastAnalyzedTimestamp = currentTimestamp
        }
    }
}*/

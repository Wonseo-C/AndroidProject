package com.example.drawingrecognition

import android.util.Log
import android.view.MotionEvent
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.MlKitException
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.*

object StrokeManager {
    private var inkBuilder = Ink.builder()
    private var strokeBuilder = Ink.Stroke.builder()
    private lateinit var model: DigitalInkRecognitionModel

    fun addNewTouchEvent(event: MotionEvent) {
        val action = event.actionMasked
        val x = event.x
        val y = event.y
        val t = System.currentTimeMillis()
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                strokeBuilder = Ink.Stroke.builder()
                strokeBuilder.addPoint(Ink.Point.create(x, y, t))
            }
            MotionEvent.ACTION_MOVE -> strokeBuilder!!.addPoint(Ink.Point.create(x, y, t))
            MotionEvent.ACTION_UP -> {
                strokeBuilder.addPoint(Ink.Point.create(x, y, t))
                inkBuilder.addStroke(strokeBuilder.build())
            }
            else -> {
                // Action not relevant for ink construction
            }
        }
    }

    fun download() {

        val remoteModelManager = RemoteModelManager.getInstance()

        val languageTag = "en"
        var modelIdentifier: DigitalInkRecognitionModelIdentifier? = null
        try {
            modelIdentifier =
                DigitalInkRecognitionModelIdentifier.fromLanguageTag(languageTag)
        } catch (e: MlKitException) {
            // language tag failed to parse, handle error.
        }

        model = DigitalInkRecognitionModel.builder(modelIdentifier!!).build()

        remoteModelManager.download(model, DownloadConditions.Builder().build())
            .addOnSuccessListener {
                Log.i("StrokeManager", "Model downloaded")
            }
            .addOnFailureListener { e: Exception ->
                Log.e("StrokeManager", "Error while downloading a model: $e")
            }
    }

    fun recognize(): Task<RecognitionResult> {
        val recognizer: DigitalInkRecognizer =
            DigitalInkRecognition.getClient(
                DigitalInkRecognizerOptions.builder(model).build()
            )

        val ink = inkBuilder.build()

        return recognizer.recognize(ink)
    }

    fun clear() {
        inkBuilder = Ink.builder()
    }

}
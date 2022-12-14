package com.example.drawingrecognition

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.digitalink.RecognitionCandidate
import com.google.mlkit.vision.digitalink.RecognitionResult

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StrokeManager.download()

        val recognize = findViewById<Button>(R.id.recognize)
        val clear = findViewById<Button>(R.id.clear)
        val drawing = findViewById<Drawing>(R.id.drawingView)
        val output = findViewById<TextView>(R.id.output)

        recognize.setOnClickListener {
            StrokeManager.recognize()
                .addOnSuccessListener { result: RecognitionResult ->
                    output.text = makeInt(result.candidates)
                }
                .addOnFailureListener { e: Exception ->
                    Log.e("StrokeManager", "Error during recognition: $e")
                }
            clear.setOnClickListener {
                output.text = ""
                drawing.clear()
                StrokeManager.clear()
            }
        }
    }

    private fun isNumeric(s: String): Boolean {
        return try {
            s.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun makeInt(candidates: List<RecognitionCandidate>): String {
        for (candidate in candidates) {
            if (isNumeric(candidate.text)
                || candidate.text.contains("-")
                || candidate.text.contains(".")
                || candidate.text.contains("+")) {
                return candidate.text
            }
        }
        return "Cannot understand your number"
    }
}
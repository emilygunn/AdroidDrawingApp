package com.example.adroiddrawingapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import yuku.ambilwarna.AmbilWarnaDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val drawingView = findViewById<DrawingView>(R.id.drawingView)

        // Load previous drawing on app start
        drawingView.loadDrawingFromJson(this)

        // Buttons
        val clearButton = findViewById<Button>(R.id.clearButton)
        val undoButton = findViewById<Button>(R.id.undoButton)
        val redoButton = findViewById<Button>(R.id.redoButton)
        val blackButton = findViewById<Button>(R.id.blackButton)
        val redButton = findViewById<Button>(R.id.redButton)
        val greenButton = findViewById<Button>(R.id.greenButton)
        val blueButton = findViewById<Button>(R.id.blueButton)
        val colorPickerButton = findViewById<Button>(R.id.colorPickerButton)
        val saveButton = findViewById<Button>(R.id.saveDrawingButton)

        clearButton.setOnClickListener { drawingView.clearCanvas() }
        undoButton.setOnClickListener { drawingView.undo() }
        redoButton.setOnClickListener { drawingView.redo() }

        blackButton.setOnClickListener { drawingView.setColor(android.graphics.Color.BLACK) }
        redButton.setOnClickListener { drawingView.setColor(android.graphics.Color.RED) }
        greenButton.setOnClickListener { drawingView.setColor(android.graphics.Color.GREEN) }
        blueButton.setOnClickListener { drawingView.setColor(android.graphics.Color.BLUE) }

        colorPickerButton.setOnClickListener {
            val dialog = AmbilWarnaDialog(
                this,
                drawingView.getCurrentColor(),
                object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                        drawingView.setColor(color)
                    }
                    override fun onCancel(dialog: AmbilWarnaDialog?) {}
                }
            )
            dialog.show()
        }

        saveButton.setOnClickListener { drawingView.saveDrawingToJson(this) }
    }
}
package com.example.adroiddrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File

@Serializable
data class SerializablePath(
    val points: List<PointF>,
    val color: Int,
    val strokeWidth: Float
)

data class DrawPath(val path: Path, val paint: Paint, val points: MutableList<PointF> = mutableListOf())

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var currentPath = Path()
    private var currentColor = Color.BLACK
    private var currentPaint = createPaint(currentColor)
    private var paths = mutableListOf<DrawPath>()
    private var undonePaths = mutableListOf<DrawPath>()

    private fun createPaint(color: Int): Paint {
        return Paint().apply {
            this.color = color
            strokeWidth = 8f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (dp in paths) canvas.drawPath(dp.path, dp.paint)
        canvas.drawPath(currentPath, currentPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path()
                currentPaint = createPaint(currentColor)
                currentPath.moveTo(x, y)
                val drawPath = DrawPath(currentPath, currentPaint)
                drawPath.points.add(PointF(x, y))
                paths.add(drawPath)
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(x, y)
                paths.lastOrNull()?.points?.add(PointF(x, y))
            }
            MotionEvent.ACTION_UP -> {
                undonePaths.clear()
            }
        }

        invalidate()
        return true
    }

    fun clearCanvas() {
        paths.clear()
        undonePaths.clear()
        currentPath.reset()
        invalidate()
    }

    fun undo() {
        if (paths.isNotEmpty()) {
            val last = paths.removeAt(paths.size - 1)
            undonePaths.add(last)
            invalidate()
        }
    }

    fun redo() {
        if (undonePaths.isNotEmpty()) {
            val path = undonePaths.removeAt(undonePaths.size - 1)
            paths.add(path)
            invalidate()
        }
    }

    fun setColor(color: Int) { currentColor = color }
    fun getCurrentColor() = currentColor

    // --- JSON save/load functions ---

    fun saveDrawingToJson(context: Context, fileName: String = "drawing.json") {
        try {
            val serializablePaths = paths.map { dp ->
                SerializablePath(
                    points = dp.points.toList(),
                    color = dp.paint.color,
                    strokeWidth = dp.paint.strokeWidth
                )
            }

            val json = Json { prettyPrint = true }
            val jsonString = json.encodeToString(serializablePaths)

            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { it.write(jsonString.toByteArray()) }
            Toast.makeText(context, "Drawing saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving drawing", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadDrawingFromJson(context: Context, fileName: String = "drawing.json") {
        try {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return

            val jsonString = file.readText()
            val json = Json { ignoreUnknownKeys = true }
            val serializablePaths = json.decodeFromString<List<SerializablePath>>(jsonString)

            paths.clear()
            serializablePaths.forEach { sp ->
                val path = Path()
                sp.points.forEachIndexed { index, point ->
                    if (index == 0) path.moveTo(point.x, point.y)
                    else path.lineTo(point.x, point.y)
                }
                val paint = createPaint(sp.color).apply { strokeWidth = sp.strokeWidth }
                paths.add(DrawPath(path, paint, sp.points.toMutableList()))
            }
            invalidate()
            Toast.makeText(context, "Drawing loaded!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error loading drawing", Toast.LENGTH_SHORT).show()
        }
    }
}
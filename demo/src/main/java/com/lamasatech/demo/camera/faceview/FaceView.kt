package com.lamasatech.demo.camera.faceview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * FaceView renders yellow rectangles over detected faces in a camera preview.
 *
 * This view is typically overlaid on top of a camera feed. Call [addFaces]
 * to provide a list of face bounding boxes (e.g., from a face detector) as [RectF] objects
 * in display coordinates. The view will invalidate and redraw itself to show all face boxes.
 *
 * You can also call [addTextFace] to draw a sample rectangle, useful for
 * UI testing or empty states, and [clear] to remove all face overlays.
 *
 * Example usage in an activity or fragment:
 * ```
 * val faceView = FaceView(context)
 * faceView.addFaces(listOf(RectF(...)))
 * ```
 *
 * This view does not handle coordinate transformations, so input rectangles
 * must already be in view coordinates.
 */
class FaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var rect: MutableList<RectF> = mutableListOf()
    private val paint = Paint()

    init {
        rect = ArrayList()
        paint.setColor(Color.YELLOW)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8.0f
    }

    /**
     * Shows a single hardcoded rectangle, mainly for testing layout appearance.
     */
    fun addTextFace(){
        rect.clear()
        rect.add(RectF(100f,100f,200f,200f))
        invalidate()
    }

    /**
     * Accepts a new list of detected face rectangles and redraws the view.
     * Each rectangle in [faces] should correspond to a detected face and be mapped to
     * the view's coordinate space. Any previous rectangles are cleared.
     *
     * @param faces A list of [RectF]s for each detected face.
     */
    fun addFaces(faces: List<RectF>) {
        rect.clear()
        if (faces.isNotEmpty()){
            rect.addAll(faces)
        }
        invalidate()
    }

    /**
     * Removes all face rectangles from the overlay and requests a redraw.
     */
    fun clear() {
        rect.clear()
        invalidate()
    }

    /**
     * Renders the current rectangles on the view's canvas.
     * Called automatically by the view system.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in rect.indices) {
            val r = rect[i]
            canvas.drawRect(r, paint)
        }
    }
}
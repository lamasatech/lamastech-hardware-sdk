package com.lamasatech.demo.camera.faceview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

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

    fun addTextFace(){
        rect.clear()
        rect.add(RectF(100f,100f,200f,200f))
        invalidate()
    }

    fun addFaces(faces: List<RectF>) {
        rect.clear()
        if (faces.isNotEmpty()){
            rect.addAll(faces)
        }
        invalidate()
    }

    fun clear() {
        rect.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in rect.indices) {
            val r = rect[i]
            canvas.drawRect(r, paint)
        }
    }
}

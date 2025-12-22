package com.lamasatech.demo.camera.faceview

import android.graphics.RectF

interface IRectMapper<T> {
    fun map(faces : Array<T>): List<RectF>
}
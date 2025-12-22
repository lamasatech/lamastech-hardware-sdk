package com.lamasatech.demo.camera.preview

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import com.lamasatech.demo.camera.provider.CameraProvider
import com.lamasatech.demo.camera.provider.ICameraProvider

@Suppress("DEPRECATION")
open class Camera1Preview @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BasePreview(context, attrs, defStyleAttr), TextureView.SurfaceTextureListener {

    protected val rgbProvider = CameraProvider(context, 0)
    private val irProvider = CameraProvider(context, 1)
    private val rgbTexture = TextureView(context)
    private var irSurfaceTexture: SurfaceTexture? = null
    private var flip = 1f
    private var orientation = 0

    init {
        addView(
            rgbTexture,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )
        rgbTexture.surfaceTextureListener = this
        configureTransform(width,height)
    }


    override fun rgbCameraProvider(): ICameraProvider = rgbProvider

    override fun irCameraProvider(): ICameraProvider = irProvider

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        rgbTexture.surfaceTextureListener = null
    }

    private fun startIrCamera() {
        if (irSurfaceTexture == null) {
            irSurfaceTexture = SurfaceTexture(10)
        }
        irSurfaceTexture?.let { irProvider.openWithSurfaceTexture(it) }
    }

    private fun stopRgbCamera() {
        rgbProvider.stop()
    }

    private fun stopIrCamera() {
        irProvider.stop()
        irSurfaceTexture?.release()
        irSurfaceTexture = null
    }

    private fun stopBoth() {
        stopRgbCamera()
        stopIrCamera()
    }

    override fun onSurfaceTextureAvailable(
        surface: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        rgbProvider.openWithSurfaceTexture(surface)
        startIrCamera()
        attachFrameListeners()
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        stopBoth()
        return false
    }

    override fun onSurfaceTextureSizeChanged(
        surface: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        // Check if size actually changed
        if (width != lastWidth || height != lastHeight) {
            lastWidth = width
            lastHeight = height

            // Restart RGB camera with new size
            rgbProvider.stop()
            configureTransform(width,height)
            rgbProvider.openWithSurfaceTexture(surface)
            attachFrameListeners()
        }
    }

    fun setConfig(degree: Int, flip: Float) {
        if (degree != orientation || this.flip != flip){
            this.orientation = degree
            this.flip = flip
            rgbProvider.stop()
            configureTransform(lastWidth,lastHeight)
            rgbProvider.openWithSurfaceTexture(rgbTexture.surfaceTexture!!)
        }
    }

    fun takePicture(imageData : (ByteArray) -> Unit){
        rgbProvider.takePicture(imageData)
    }

    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        val previewSize = rgbProvider.getSize() ?: return
        val previewWidth = previewSize.width
        val previewHeight = previewSize.height
        if (previewWidth == 0 || previewHeight == 0) return

        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, previewWidth.toFloat(), previewHeight.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        // Center buffer
        bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
        matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)

        // Scale to fill
        val scale = maxOf(
            viewHeight.toFloat() / previewHeight,
            viewWidth.toFloat() / previewWidth
        )
        matrix.postScale(scale, scale, centerX, centerY)

        // Rotation
        matrix.postRotate(orientation.toFloat(), centerX, centerY)

        // Flip inside matrix instead of scaleX
        matrix.postScale(flip, 1f, centerX, centerY)

        // Always post to UI thread to avoid blocking the camera rendering thread
        rgbTexture.post {
            rgbTexture.setTransform(matrix)
        }

    }

}




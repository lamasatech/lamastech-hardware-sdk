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

/**
 * Camera1Preview is a concrete implementation of [BasePreview] that handles
 * preview and configuration for a dual-camera (RGB + IR) setup using Android's
 * legacy Camera1 API.
 *
 * - Handles surface texture events for camera preview.
 * - Automatically configures aspect ratio, orientation, and flip for visual output.
 * - Allows for preview configuration and taking still shots.
 *
 * @constructor Create a [Camera1Preview] that manages RGB and IR streams.
 */
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

    /** Returns RGB camera provider. */
    override fun rgbCameraProvider(): ICameraProvider = rgbProvider

    /** Returns IR camera provider. */
    override fun irCameraProvider(): ICameraProvider = irProvider

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        rgbTexture.surfaceTextureListener = null
    }

    /** Opens the IR camera and attaches its SurfaceTexture. */
    private fun startIrCamera() {
        if (irSurfaceTexture == null) {
            irSurfaceTexture = SurfaceTexture(10)
        }
        irSurfaceTexture?.let { irProvider.openWithSurfaceTexture(it) }
    }

    /** Stops the RGB camera. */
    private fun stopRgbCamera() {
        rgbProvider.stop()
    }

    /** Stops the IR camera and releases its SurfaceTexture. */
    private fun stopIrCamera() {
        irProvider.stop()
        irSurfaceTexture?.release()
        irSurfaceTexture = null
    }

    /** Stops both RGB and IR cameras. */
    private fun stopBoth() {
        stopRgbCamera()
        stopIrCamera()
    }

    /**
     * Handles when the RGB preview surface becomes available. Starts both RGB and IR cameras.
     */
    override fun onSurfaceTextureAvailable(
        surface: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        rgbProvider.openWithSurfaceTexture(surface)
        startIrCamera()
        attachFrameListeners()
    }

    /**
     * Called when the surface texture is destroyed. Stops both cameras.
     */
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        stopBoth()
        return false
    }

    /**
     * Handles changes in preview surface size and restarts cameras if the size changed.
     */
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

    /**
     * Sets preview orientation and horizontal flip configuration for the RGB camera.
     * @param degree orientation in degrees.
     * @param flip horizontal flip scale (-1f for mirror, 1f for normal).
     */
    fun setConfig(degree: Int, flip: Float) {
        if (degree != orientation || this.flip != flip){
            this.orientation = degree
            this.flip = flip
            rgbProvider.stop()
            configureTransform(lastWidth,lastHeight)
            rgbProvider.openWithSurfaceTexture(rgbTexture.surfaceTexture!!)
        }
    }

    /**
     * Captures the current frame from the RGB camera and returns the raw bytes.
     * @param imageData callback invoked with JPEG image data.
     */
    fun takePicture(imageData : (ByteArray) -> Unit){
        rgbProvider.takePicture(imageData)
    }

    /**
     * Configures the transformation matrix to maintain aspect ratio, orientation,
     * and optional horizontal flip for the RGB camera preview.
     * @param viewWidth width of the preview surface.
     * @param viewHeight height of the preview surface.
     */
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



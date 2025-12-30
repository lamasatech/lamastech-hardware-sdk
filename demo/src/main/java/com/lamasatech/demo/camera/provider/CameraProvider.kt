package com.lamasatech.demo.camera.provider

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import kotlin.math.abs

/**
 * CameraProvider wraps Android's legacy Camera1 API for handling preview
 * and picture capture from a specified device camera (by id).
 *
 * Provides methods for opening cameras with either SurfaceHolder or
 * SurfaceTexture, listening to preview frames, setting the best preview size,
 * and correcting orientation.
 *
 * @property context The Android Context.
 * @property cameraId The camera ID (0 for RGB, 1 for IR).
 */
@Suppress("DEPRECATION")
class CameraProvider(
    val context: Context,
    val cameraId: Int
) : ICameraProvider {

    private var camera: Camera? = null
    private var listener: ((data: ByteArray, width: Int, height: Int) -> Unit)? = null
    private val previewFormat = ImageFormat.NV21

    private var size: Camera.Size? = null

    /**
     * Sets the callback for frame delivery.
     */
    override fun setListener(l: (data: ByteArray, width: Int, height: Int) -> Unit) {
        listener = l
    }

    /**
     * Returns the preview size, or null if not set.
     */
    fun getSize(): Camera.Size? = size

    /**
     * Open camera with SurfaceHolder (SurfaceView).
     * @param holder The SurfaceHolder to attach the camera preview to.
     */
    fun openWithSurfaceHolder(holder: SurfaceHolder) {
        openInternal { cam ->
            cam.setPreviewDisplay(holder)
        }
    }

    /**
     * Open camera with SurfaceTexture (offscreen or TextureView).
     * @param surfaceTexture The SurfaceTexture to attach the camera preview to.
     */
    fun openWithSurfaceTexture(surfaceTexture: SurfaceTexture) {
        openInternal { cam ->
            cam.setPreviewTexture(surfaceTexture)
        }
    }

    /**
     * Internal helper to open and configure the camera, attach surface, and start preview.
     */
    private fun openInternal(attachSurface: (Camera) -> Unit) {
        try {
            if (camera != null) stop()
            camera = Camera.open(cameraId).apply {
                updateParameter()

                //fix orientation
                val orientation = calculateDisplayOrientation()
                setDisplayOrientation(orientation)

                size = parameters.previewSize

                // Preview callback with buffer (efficient)
                val frameSize =
                    size!!.width * size!!.height * ImageFormat.getBitsPerPixel(previewFormat) / 8
                repeat(3) { addCallbackBuffer(ByteArray(frameSize)) }

                setPreviewCallbackWithBuffer { data, cam ->
                    listener?.invoke(data, size!!.width, size!!.height)
                    cam.addCallbackBuffer(data)
                }

                attachSurface(this)
                startPreview()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stop()
        }
    }

    /**
     * Captures a JPEG still image and passes it to the callback.
     * @param imageData Callback invoked with JPEG data.
     */
    fun takePicture(imageData : (ByteArray) -> Unit){
        camera?.takePicture(null,null) { data, camera ->
            imageData.invoke(data)
            camera.startPreview()
        }
    }

    /**
     * Stops and releases the camera (preview and all resources).
     */
    fun stop() {
        camera?.apply {
            runCatching { setPreviewCallbackWithBuffer(null) }.onFailure { it.printStackTrace() }
            runCatching { stopPreview() }.onFailure { it.printStackTrace() }
            runCatching { release() }.onFailure { it.printStackTrace() }
        }
        camera = null
        listener = null
    }

    /**
     * Updates camera parameters (format, size, orientation, etc.) using display info.
     */
    private fun Camera.updateParameter() {
        val params = parameters

        //format
        params.previewFormat = previewFormat

        //size
        // fallback safe list
        val supportedSizes = params.supportedPreviewSizes?.takeIf { it.isNotEmpty() }
            ?: listOf(params.previewSize)

        val display =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val targetWidth = display.width
        val targetHeight = display.height

        val bestSize = getBestPreviewSize(supportedSizes, targetWidth, targetHeight)
        params.setPreviewSize(bestSize.width, bestSize.height)


        //update
        parameters = params
    }

    /**
     * Chooses the preview size that best matches the device screen.
     */
    private fun getBestPreviewSize(
        sizes: List<Camera.Size>,
        targetWidth: Int,
        targetHeight: Int
    ): Camera.Size {
        val aspectTolerance = 0.1
        val targetRatio = targetWidth.toDouble() / targetHeight

        var best: Camera.Size? = null
        var minDiff = Double.MAX_VALUE

        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (abs(ratio - targetRatio) > aspectTolerance) continue
            val diff = abs(size.height - targetHeight)
            if (diff < minDiff) {
                best = size
                minDiff = diff.toDouble()
            }
        }

        return best ?: sizes.minByOrNull { abs(it.height - targetHeight) } ?: sizes[0]
    }


    /**
     * Computes the display orientation based on device rotation and camera orientation.
     */
    private fun calculateDisplayOrientation(): Int {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)

        val rotation = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.rotation

        val degrees = when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }

        return if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            (360 - (info.orientation + degrees) % 360) % 360 // mirror compensation
        } else {
            (info.orientation - degrees + 360) % 360
        }
    }

}

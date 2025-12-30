package com.lamasatech.demo.camera.preview

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.TextureView
import android.widget.FrameLayout
import com.lamasatech.demo.camera.Frame
import com.lamasatech.demo.camera.FrameFlow
import com.lamasatech.demo.camera.FrameProcessor
import com.lamasatech.demo.camera.provider.ICameraProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

/**
 * Abstract view for compositing dual camera (RGB + IR) previews and processing their frames.
 *
 * BasePreview manages surface callbacks, camera frame synchronization, and delivers [DualFrame]s
 * to registered [FrameProcessor]s. Subclasses provide the specific [ICameraProvider]s.
 *
 * It sets up a coroutine to collect [DualFrame]s as the preview is running, ensures
 * processing state is managed, and notifies child classes of size/orientation changes.
 *
 * **Intended for use as a view within an Activity or Fragment that needs to show video previews**
 * from multiple cameras, and process those in near-realtime (e.g. for face recognition).
 *
 * Override [onDimensionsChanged] to respond to view size changes. Use [setFrameProcessor]
 * to receive camera frames.
 */
abstract class BasePreview @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), TextureView.SurfaceTextureListener,
    SurfaceHolder.Callback {

    /**
     * The width and height of the view after the last dimensions change.
     */
    protected var lastWidth = 0
    protected var lastHeight = 0

    private val previewFlow = FrameFlow()
    private var frameJob: Job? = null
    private val viewScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var frameProcessor: FrameProcessor? = null

    /**
     * Provide the implementation for the RGB camera.
     */
    abstract fun rgbCameraProvider(): ICameraProvider

    /**
     * Provide the implementation for the IR (infrared) camera.
     */
    abstract fun irCameraProvider(): ICameraProvider

    /**
     * Called when the size of the preview changes (e.g., orientation or layout change).
     * Override this to update overlays or camera configuration.
     */
    open fun onDimensionsChanged(width: Int, height: Int){}

    /**
     * Sets up the preview coroutine, which emits [DualFrame]s as frames are received from cameras,
     * and passes them to [frameProcessor].
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        frameJob = viewScope.launch {
            previewFlow.latestPair.collect {
                if (it != null) {
                    previewFlow.startFrameProcess()
                    val processTimeMills = measureTimeMillis {
                        frameProcessor?.process(it)
                    }
                    previewFlow.endFrameProcess()
                }
            }
        }
    }

    /**
     * Cancels the preview coroutine and releases resources when the view is detached.
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        frameJob?.cancel()
    }

    /**
     * Assigns a [FrameProcessor] callback that will be invoked for every paired RGB/IR frame.
     */
    fun setFrameProcessor(processor: FrameProcessor) {
        frameProcessor = processor
    }

    /**
     * Attaches listeners to both RGB and IR camera providers, such that each incoming frame
     * is added to the [FrameFlow] for synchronization and dispatch.
     */
    protected fun attachFrameListeners() {
        rgbCameraProvider().setListener { data, width, height ->
            previewFlow.addRgbFrame(Frame(data, width, height))
        }
        irCameraProvider().setListener { data, width, height ->
            previewFlow.addIrFrame(Frame(data, width, height))
        }
    }

    // Surface and texture event handlers — override as required
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        checkDimensionsChanged(width, height)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        checkDimensionsChanged(width, height)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true
    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    override fun surfaceCreated(holder: SurfaceHolder) {
        checkDimensionsChanged(holder.surfaceFrame.width(), holder.surfaceFrame.height())
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
        checkDimensionsChanged(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    /**
     * Checks if the view dimensions have changed. If yes, calls [onDimensionsChanged].
     */
    private fun checkDimensionsChanged(width: Int, height: Int) {
        if (width != lastWidth || height != lastHeight) {
            lastWidth = width
            lastHeight = height
            onDimensionsChanged(width, height)
        }
    }
}
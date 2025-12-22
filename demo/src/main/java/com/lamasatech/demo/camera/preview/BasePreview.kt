package com.lamasatech.demo.camera.preview

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
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

abstract class BasePreview @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), TextureView.SurfaceTextureListener,
    SurfaceHolder.Callback {

    protected var lastWidth = 0

    protected var lastHeight = 0
    private val previewFlow = FrameFlow()
    private var frameJob: Job? = null
    private val viewScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var frameProcessor: FrameProcessor? = null

    abstract fun rgbCameraProvider(): ICameraProvider
    abstract fun irCameraProvider(): ICameraProvider

    open fun onDimensionsChanged(width: Int, height: Int){}

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        frameJob = viewScope.launch {
            previewFlow.latestPair.collect {
                if (it != null) {
                    // Process frame
                    previewFlow.startFrameProcess()
                    val processTimeMills = measureTimeMillis {
                        frameProcessor?.process(it)
                    }
                    previewFlow.endFrameProcess()
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        frameJob?.cancel()
    }

    fun setFrameProcessor(processor: FrameProcessor) {
        frameProcessor = processor
    }

    protected fun attachFrameListeners() {
        rgbCameraProvider().setListener { data, width, height ->
            previewFlow.addRgbFrame(Frame(data, width, height))
        }
        irCameraProvider().setListener { data, width, height ->
            previewFlow.addIrFrame(Frame(data, width, height))
        }
    }

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

    private fun checkDimensionsChanged(width: Int, height: Int) {
        if (width != lastWidth || height != lastHeight) {
            lastWidth = width
            lastHeight = height
            onDimensionsChanged(width, height)
        }
    }

}
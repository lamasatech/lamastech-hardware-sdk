package com.lamasatech.demo.camera.provider

/**
 * General contract for any camera provider delivering preview frames to consumers.
 *
 * Implementations should invoke the registered listener callback whenever a new camera frame
 * is available. The callback receives the raw byte array and the resolution (width, height).
 * Use this in combination with classes like [CameraProvider] to abstract how frames are collected.
 */
interface  ICameraProvider {
    /**
     * Registers a callback to be invoked when a new camera frame is available.
     * @param l Listener taking frame bytes and resolution.
     */
    fun setListener(l: (data:ByteArray,width: Int, height:Int) -> Unit)
}
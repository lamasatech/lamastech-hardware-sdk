package com.lamasatech.demo.camera

/**
 * Represents a single image frame captured by a camera device.
 *
 * Use this to encapsulate pixel data along with frame dimensions. Typically
 * frames are raw YUV, NV21, or similar formats as delivered by the camera APIs.
 *
 * @property data Raw byte array holding the pixels for the frame.
 * @property width Width (in pixels) of the frame.
 * @property height Height (in pixels) of the frame.
 */
@Suppress("ArrayInDataClass")
data class Frame(val data: ByteArray, val width: Int, val height: Int)

/**
 * Holds a synchronized pair of frames—one from the RGB camera and one from the IR camera.
 *
 * The intent is to represent a point-in-time capture from both sensors, allowing for
 * multi-spectral processing like face analysis, anti-spoofing, or depth perception.
 *
 * @property rgb The image captured from the standard (visible light) camera.
 * @property ir The image captured from the infrared camera.
 * @property count Sequential frame index since start of preview/session for tracking.
 */
data class DualFrame(val rgb: Frame, val ir: Frame, val count: Int)
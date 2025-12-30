package com.lamasatech.demo.camera.faceview

import android.graphics.RectF

/**
 * Maps data from a face detector (or any detection type) into rectangles for UI display.
 *
 * This interface allows you to customize how detection results are shown visually:
 * for instance, mapping SDK face objects or MLKit objects into [RectF]s in the preview's coordinate system.
 *
 * Implement this to convert face detections to rectangles that [FaceView] can show.
 *
 * Example:
 * ```
 * class MlKitRectMapper : IRectMapper<Face> {
 *   override fun map(faces: Array<Face>): List<RectF> {
 *      // convert Face objects to rectangles
 *   }
 * }
 * ```
 *
 * @param T The detection type (e.g., MLKit Face, custom detection class, etc.)
 */
interface IRectMapper<T> {
    /**
     * Converts an array of detection objects ([faces]) into a list of rectangles in view/result space.
     * @param faces Array of detection results from a detector.
     * @return List of on-screen rectangles to display as overlays.
     */
    fun map(faces : Array<T>): List<RectF>
}
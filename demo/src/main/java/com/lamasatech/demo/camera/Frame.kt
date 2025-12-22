package com.lamasatech.demo.camera

@Suppress("ArrayInDataClass")
data class Frame(val data: ByteArray, val width: Int, val height: Int)
data class DualFrame(val rgb: Frame, val ir: Frame, val count: Int)
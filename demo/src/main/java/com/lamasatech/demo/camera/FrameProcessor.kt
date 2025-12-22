package com.lamasatech.demo.camera

interface  FrameProcessor {
    suspend fun process(frame: DualFrame)
}
package com.lamasatech.demo.camera.provider

interface  ICameraProvider {
    fun setListener(l: (data:ByteArray,width: Int, height:Int) -> Unit)
}